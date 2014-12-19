var lazy = require("lazy");
var fs = require("fs");

var v3 = require('../target/main/migration-node.js')
var Cloudant = require('cloudant');

// Usage:
// $ node migration.js DBS CLOUDANT_USER CLOUDANT_PASSWORD

var dbs = process.argv[2];
var me = process.argv[3];
var password = process.argv[4];

var saveDocs = function(new_db, new_docs) {
  var doc_ids = "[";
   for(var i = 0; i<new_docs.length; i++) {
    doc_ids += new_docs[i]["_id"];
    if(i < new_docs.length - 1) {
      doc_ids += ", ";
    }
   }
   doc_ids += "]";

   new_db.bulk({'docs' : new_docs}, function(err, body, header) {
     if (err) {
       console.log("ERROR - inserting documents " + doc_ids + " - " + err);
       process.exit(1);
     }

     console.log("MIGRATE: " + doc_ids + " to " + new_db.config.db)
   });
}

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

     new lazy(fs.createReadStream(dbs))
         .lines
         .forEach(function(line){

             var db_name = line.toString();
             var user_id = db_name.substring(3, db_name.length);
             console.log("User ID: " + user_id)
             var suffix = "-v3";

             console.log("Migrating database: " + db_name);

             var old_db = cloudant.use(db_name);
             var new_db_name = db_name + suffix;

             // Create a new database
             cloudant.db.create(new_db_name, function(err) {
               // TODO Figure out if we want to quit if the db already exists or not (they all do right now because I've run the script :)
               if (err) {
                 if(err.status_code != 412) {
                   console.log("ERROR - creating database: " + new_db_name + " - " + err);
                 }
               }
             });

             var new_db = cloudant.use(new_db_name);

             var new_docs = [];

             var BLOCK_SIZE = 1000;
             convert_block(old_db, BLOCK_SIZE, null, new_db, user_id);
         }
     );
  });
});

function convert_doc(db, doc, new_db, user_id) {
    if(doc.id.indexOf("_design") != 0) {               //skip _design docs
        if(doc.doc.version === 3) {
            saveDocs(new_db, [doc.doc]);
        } else {

            // we have to treat Sources specially so that they get is_root
            if(doc.doc.type && doc.doc.type === "Source") {
                // see if this is a parent
                db.view('EntityBase', 'parent_sources', {"keys" : [doc.doc._id]}, function(err, body) {
                    if(err) {
                        console.log("ERROR - getting source parents " + doc.doc._id + " - " + err);
                        process.exit(1);
                    }

                    if(body.rows.length == 0) {
                        doc.doc.is_root = true;
                    } else {
                        doc.doc.is_root = false;
                    }

                    saveDocs(new_db, v3.migration.node.migrate(doc.doc, user_id));
                });
            } else {
                saveDocs(new_db, v3.migration.node.migrate(doc.doc, user_id));
            }
        }
    }
}

function convert_block(db, block_size, startkey_id, new_db, user_id) {
    console.log("Migrating block from " + startkey_id);
    params = {"include_docs": true, "limit": block_size + 1};
    if(startkey_id) {
        params.startkey_id = startkey_id;
        params.startkey = startkey_id;
    }
    
    var doc;
    
    db.list(params, function(err, body) {
        if (!err) {
            console.log(body.rows.length + " (of " + body.total_rows + " in " + block_size + ")");
            for(var i = 0; i < body.rows.length - 1; i++) {
                doc = body.rows[i];
                convert_doc(db, doc, new_db, user_id);
            }
            
            if(body.rows.length <= block_size) {
                doc = body.rows[body.rows.length-1];
                convert_doc(db, doc, new_db, user_id);
            } else {
                doc = body.rows[body.rows.length-1];
                convert_block(db, block_size, doc.id, new_db, user_id);    
            }
        } else {
            console.log("ERROR - listing DB documents: " + err);
            process.exit(1);
        }
    });
}
