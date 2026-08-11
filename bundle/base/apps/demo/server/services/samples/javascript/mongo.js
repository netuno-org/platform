/**
 *
 *  EN: MONGODB
 *  EN: Shows how to work with MongoDB in Netuno: connecting, using databases
 *  EN: and collections, inserting and querying documents, filtering, sorting,
 *  EN: projecting, updating, replacing, deleting, counting, indexing and
 *  EN: aggregating.
 *
 *  PT: MONGODB
 *  PT: Mostra como trabalhar com MongoDB no Netuno: ligar, usar bases de dados
 *  PT: e coleções, inserir e consultar documentos, filtrar, ordenar, projetar,
 *  PT: atualizar, substituir, apagar, contar, indexar e agregar.
 *
 */

/*
 *  Connect to MongoDB
 *  You can pass the name of a connection defined in the application
 *  config file (the "mongo" block) or a connection URL directly.
 */

const mongo = _mongo.init("products");

/*
 *  Get the database and the collection
 *  The database is created automatically and the collection only needs to
 *  be created once.
 */

const database = mongo.database("product_db")

database.createCollection("product")

const collection = database.collection("product")

_out.println(`<pre>`)
_out.json(database.collectionNames())
_out.println(`</pre>`)

/*
 *  Insert a single document
 *  Documents are built with _val.map() and _val.list().
 */

_out.println(`<h4>Insert</h4>`)

const laptopId = collection.insertOne(
  _val.map()
    .set("name", "Laptop")
    .set("quantity", 22)
    .set("price", 100)
    .set("category", "computers")
    .set(
      "tags",
      _val.list()
        .add("promo")
        .add("new")
    )
)

_out.println(`<p>Inserted id: ${laptopId}</p>`)

/*
 *  Insert multiple documents at once
 */

const ids = collection.insertMany(
  _val.list()
    .add(
      _val.map()
        .set("name", "Tablet")
        .set("quantity", 24)
        .set("price", 200)
        .set("category", "computers")
    )
    .add(
      _val.map()
        .set("name", "Smartphone")
        .set("quantity", 18)
        .set("price", 200)
        .set("category", "smartphones")
    )
)

_out.println(`<pre>`)
_out.json(ids)
_out.println(`</pre>`)

/*
 *  Find documents
 *  find() accepts an optional filter, all() returns every match and
 *  first() returns only the first one (or null if there is none).
 */

_out.println(`<h4>Find all</h4>`)

_out.println(`<pre>`)
_out.json(collection.find().all())
_out.println(`</pre>`)

_out.println(`<h4>Find with a filter</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.find(
    _mongo.filters().eq("name", "Laptop")
  ).all()
)
_out.println(`</pre>`)

_out.println(`<h4>Find first match</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.find(
    _mongo.filters().eq("name", "Laptop")
  ).first()
)
_out.println(`</pre>`)

/*
 *  Combine filters
 *  Filters can be combined with and(), or() and not().
 */

_out.println(`<h4>Filter: price &gt; 50 and quantity &lt; 30</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.find(
    _mongo.filters().and(
      _mongo.filters().gt("price", 50),
      _mongo.filters().lt("quantity", 30)
    )
  ).all()
)
_out.println(`</pre>`)

_out.println(`<h4>Filter: name matches a regular expression</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.find(
    _mongo.filters().regex("name", "^[LS]")
  ).all()
)
_out.println(`</pre>`)

/*
 *  Iterate over the results
 *  You can use a for loop or forEach().
 */

_out.println(`<h4>Iterate (names)</h4>`)

_out.println(`<ul>`)
for (const doc of collection.find().all()) {
  _out.println(`<li>${doc.getString("name")}</li>`)
}
_out.println(`</ul>`)

_out.println(`<ul>`)
collection.find().forEach((doc) => {
  _out.println(`<li>forEach -> ${doc.getString("name")}</li>`)
})
_out.println(`</ul>`)

/*
 *  Sort the results
 *  Use ascending(), descending() or orderBy() for multiple fields.
 */

_out.println(`<h4>Sort by price (descending)</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.find().sort(
    _mongo.sorts().descending("price")
  ).all()
)
_out.println(`</pre>`)

_out.println(`<h4>Sort by multiple fields</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.find().sort(
    _mongo.sorts().orderBy(
      _mongo.sorts().descending("price"),
      _mongo.sorts().ascending("quantity")
    )
  ).all()
)
_out.println(`</pre>`)

/*
 *  Project the fields to return
 *  Only the listed fields are returned, saving bandwidth.
 */

_out.println(`<h4>Projection: only name and quantity</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.find().projection(
    _mongo.projections().include("name", "quantity")
  ).all()
)
_out.println(`</pre>`)

/*
 *  Limit and skip
 *  limit() limits the number of results and skip() ignores the first ones,
 *  which is useful for pagination.
 */

_out.println(`<h4>Limit and skip (pagination)</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.find()
    .sort(_mongo.sorts().ascending("name"))
    .skip(1)
    .limit(2)
    .all()
)
_out.println(`</pre>`)

/*
 *  Update documents
 *  updateOne() changes the first match and updateMany() changes every match.
 *  The changes are described with _mongo.updates().
 */

_out.println(`<h4>Update one</h4>`)

collection.updateOne(
  _mongo.filters().eq("name", "Laptop"),
  _mongo.updates().set("quantity", 42)
)

_out.println(`<pre>`)
_out.json(
  collection.find(
    _mongo.filters().eq("name", "Laptop")
  ).all()
)
_out.println(`</pre>`)

_out.println(`<h4>Update many</h4>`)

collection.updateMany(
  _mongo.filters().eq("category", "computers"),
  _mongo.updates().set("category", "featured")
)

_out.println(`<pre>`)
_out.json(collection.find().all())
_out.println(`</pre>`)

/*
 *  Combine several updates in a single operation
 */

_out.println(`<h4>Combined updates</h4>`)

collection.updateOne(
  _mongo.filters().eq("name", "Laptop"),
  _mongo.updates().combine(
    _mongo.updates().set("quantity", 7),
    _mongo.updates().push("tags", "sale")
  )
)

_out.println(`<pre>`)
_out.json(
  collection.find(
    _mongo.filters().eq("name", "Laptop")
  ).all()
)
_out.println(`</pre>`)

/*
 *  Find and update
 *  findOneAndUpdate() updates the document atomically and returns the
 *  original one before the change.
 */

_out.println(`<h4>Find one and update</h4>`)

const oldLaptop = collection.findOneAndUpdate(
  _mongo.filters().eq("name", "Laptop"),
  _mongo.updates().set("quantity", 42)
)

_out.println(`<pre>`)
_out.json(oldLaptop)
_out.println(`</pre>`)

/*
 *  Replace a document
 *  replaceOne() replaces the whole document (unlike update, it cannot use
 *  update operators).
 */

_out.println(`<h4>Replace one</h4>`)

collection.replaceOne(
  _mongo.filters().eq("name", "Laptop"),
  _val.map()
    .set("name", "Laptop")
    .set("quantity", 50)
    .set("price", 150)
    .set("category", "featured")
)

_out.println(`<pre>`)
_out.json(
  collection.find(
    _mongo.filters().eq("name", "Laptop")
  ).all()
)
_out.println(`</pre>`)

_out.println(`<h4>Find one and replace</h4>`)

const oldTablet = collection.findOneAndReplace(
  _mongo.filters().eq("name", "Tablet"),
  _val.map()
    .set("name", "Tablet")
    .set("quantity", 12)
    .set("price", 250)
    .set("category", "computers")
)

_out.println(`<pre>`)
_out.json(oldTablet)
_out.println(`</pre>`)

/*
 *  Count documents
 *  countDocuments() accepts an optional filter.
 */

_out.println(`<h4>Count</h4>`)

const computers = collection.countDocuments(
  _mongo.filters().eq("category", "computers")
)

_out.println(`<p>Total: ${collection.countDocuments()}</p>`)
_out.println(`<p>Computers: ${computers}</p>`)
_out.println(`<p>Estimated: ${collection.estimatedDocumentCount()}</p>`)

/*
 *  Create indexes
 *  Indexes speed up the queries on the indexed fields.
 */

_out.println(`<h4>Indexes</h4>`)

collection.createIndex(
  _mongo.indexes().ascending("quantity")
)

collection.createIndex(
  _mongo.indexes().compoundIndex(
    _mongo.indexes().descending("price"),
    _mongo.indexes().ascending("quantity")
  )
)

_out.println(`<pre>`)
_out.json(
  collection.find()
    .hint(
      _mongo.valToDoc(
        _val.map().set("quantity", 1)
      )
    )
    .all()
)
_out.println(`</pre>`)

/*
 *  Aggregate
 *  The aggregation pipeline groups and processes the documents in stages.
 */

_out.println(`<h4>Aggregate: total price per category</h4>`)

_out.println(`<pre>`)
_out.json(
  collection.aggregate(
    _mongo.aggregates().group(
      "$category",
      _mongo.accumulators().sum("total", "$price")
    ),
    _mongo.aggregates().sort(
      _mongo.sorts().descending("total")
    )
  ).all()
)
_out.println(`</pre>`)

/*
 *  Delete documents
 */

_out.println(`<h4>Delete one</h4>`)

collection.deleteOne(
  _mongo.filters().eq("name", "Laptop")
)

_out.println(`<h4>Find one and delete</h4>`)

const removed = collection.findOneAndDelete(
  _mongo.filters().eq("name", "Tablet")
)

_out.println(`<pre>`)
_out.json(removed)
_out.println(`</pre>`)

/*
 *  Convert between Netuno values and BSON documents
 */

_out.println(`<h4>Values &lt;-&gt; BSON</h4>`)

const bsonDoc = _mongo.valToDoc(
  _val.map()
    .set("name", "Laptop")
    .set("quantity", 22)
)

const valuesFromBson = _mongo.docToVal(bsonDoc)

_out.println(`<pre>`)
_out.json(valuesFromBson)
_out.println(`</pre>`)

/*
 *  Clean up and close the connection
 *  Removing every document keeps the sample ready to run again.
 */

collection.deleteMany(_mongo.valToDoc(_val.map()))

_out.println(`<p>Remaining documents: ${collection.countDocuments()}</p>`)

_mongo.close()
