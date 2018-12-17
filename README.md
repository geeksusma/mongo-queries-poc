# mongo-queries-poc
Playing with MongoTemplate about paginating queries, filtering by date range, basic joins and so on

## Tests
Using an embedded MongoDB you can find here a Test class with some examples, basically those examples are for:

* Adding pagination to a resultset
* Filtering by a exactly match
* Filtering by a Range using Java 8 date time objects
* Example about how to do a LEFT JOIN between documents using "lookup"
* (!) Keep in mind the next! "lookup" was introduced in MongoDB 3.2, so this way of executing a LEFT JOIN only would work in versions above 3.2.x

### Local usage instructions:

#### [Lombok](https://projectlombok.org/)

  We use lombok to clean boilerplate code from DTO, Model and Domain entities. You need a plugin to use it into:
  * Eclipse -> [Lombok Eclipse plugin](https://projectlombok.org/setup/eclipse)
  * Intellij -> [Lombok Intellij plugin](https://projectlombok.org/setup/intellij)

  This plugin only provides information to IDE about generated code for auto-complete and analyses code functionality
