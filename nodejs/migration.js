var Cloudant = require('cloudant');
var v3 = require('./target/main/migration-node.js')

// Usage:
// $ node migration.js CLOUDANT_USER CLOUDANT_PASSWORD

var me = process.argv[2];
var password = process.argv[3];

Cloudant({account:me, password:password}, function(er, cloudant) {
  if (er) {
    return console.log('Error connecting to Cloudant account %s: %s', me, er.message);
  }

  console.log('Connected to cloudant');
  cloudant.ping(function(er, reply) {
    if (er) {
      return console.log('Failed to ping Cloudant. Did the network just go down?');
    }

    // Just printing out some user info
    console.log('Server version = %s', reply.version);
    console.log('I am %s and my roles are %j', reply.userCtx.name, reply.userCtx.roles);

    // Get the list of all databases
    cloudant.db.list(function(er, all_dbs) {
      if (er) {
        return console.log('Error listing databases: %s', er.message)
      }

      // Iterate through each db for this user
      for (var i = 0; i < all_dbs.length; i++) {
        var db_name = all_dbs[i];

        // only do dbs with "db-"
        var prefix = "db-";
        if (db_name.slice(0, prefix.length) != prefix) {
          //console.log("fail " + db_name);
          continue;
        }

        // also skip dbs that end in -v3!
        var suffix = "-v3";
        if (db_name.indexOf(suffix, db_name.length - suffix.length) !== -1) {
          //console.log("suffix fail " + db_name);
          continue;
        }

        console.log("Migrating database: " + db_name);

        var old_db = cloudant.use(db_name);
        var new_db_name = db_name + suffix;

        // Create a new database
        cloudant.db.create(new_db_name, function(err) {
          // TODO Figure out if we want to quit if the db already exists or not (they all do right now because I've run the script :)
          if (err) {
            console.log("ERROR - creating database: " + new_db_name + " - " + err);
            //process.exit(1);
          }

          var new_db = cloudant.use(new_db_name);

          old_db.list(function(err, body) {
            if (!err) {
              body.rows.forEach(function(doc) {
                console.log(doc);

                // Put migration code here

                var new_docs = v3.migration.core.migrate(doc); // <-- MIGRATE FUNCTION HERE!
                var doc_ids = "[";
                for(var i = 0; i<new_docs.length; i++) {
                 doc_ids += ", " + new_docs[i]["_id"];
                }
                doc_ids += "]";

                new_db.bulk(new_docs, function(err, body, header) {
                  if (err) {
                    console.log("ERROR - inserting documents " + doc_ids + " - " + err);
                    process.exit(1);
                  }

                  console.log("MIGRATE: " + doc_ids)
                });
              });
            }
          });
        });
      }
    });
  });
});
