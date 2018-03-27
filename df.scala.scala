// import org.apache.spark.sql.SparkSession
// val spark = SparkSession.builder().getOrCreate()
// val df = spark.read.option("header", "true").option("inferSchema", "true").csv("CitiGroup2006_2008")
//
// df.printSchema()
//
// import spark.implicits._
//
// // df.filter("Close>480").show()
//
// // val CH_LOW = df.filter($"Close" < 480 && $"High" < 480).count()
//
// df.filter($"High" === 484.40).show()
//
// df.select(corr("High", "Low")).show()

// import org.apache.spark.sql.SparkSession
// val spark = SparkSession.builder().getOrCreate()
// // val df = spark.read.option("header", "true").option("inferSchema", "true").csv("CitiGroup2006_2008")
//
// val df = spark.read.option("header","true").option("inferSchema","true").csv("ContainsNull.csv")
//
// df.show()
// val df2 = df.na.fill(100, Array("Sales")).show()
// df2.na.fill("Name", Array("Name")).show()
// Dates and TimeStamps

// Start a simple Spark Session
import org.apache.spark.sql.SparkSession
val spark = SparkSession.builder().getOrCreate()
val df = spark.read.option("header","true").option("inferSchema","true").csv("Netflix_2011_2016.csv")

// Start a simple Spark Session

// Load the Netflix Stock CSV File, have Spark infer the data types.

// What are the column names?
// Date| Open| High|      Low|            Close|   Volume|         Adj Close|

// What does the Schema look like?
df.printSchema()
// |-- Date: timestamp (nullable = true)
// |-- Open: double (nullable = true)
// |-- High: double (nullable = true)
// |-- Low: double (nullable = true)
// |-- Close: double (nullable = true)
// |-- Volume: integer (nullable = true)
// |-- Adj Close: double (nullable = true)

// Print out the first 5 columns.
// df.head(5)

for (i <- df.head(5)){
  println(i)
}
// Use describe() to learn about the DataFrame.
// df.describe()
// Create a new dataframe with a column called HV Ratio that
// is the ratio of the High Price versus volume of stock traded
// for a day.
val df2 = df.withColumn("HV Ratio", df("Volume")/df("High"))
df2.show()
// What day had the Peak High in Price?
df2.groupBy().max("High", "Date").show()
// What is the mean of the Close column?
df2.groupBy().mean("CLose").show()

// What is the max and min of the Volume column?
df2.groupBy().max("Volume").show()
df2.groupBy().min("Volume").show()

// For Scala/Spark $ Syntax
import spark.implicits._
// How many days was the Close lower than $ 600?
df2.filter($"Close" < 600).count()
// What percentage of the time was the High greater than $500 ?
(df.filter($"High">500).count()*1.0/df.count())*100
// What is the Pearson correlation between High and Volume?
df2.select(corr("High", "Volume")).show()
// What is the max High per year?
// val df3 = df2.withColumn("Year", year(df("Date")))
// val dfavgs = df3.groupBy("Year").max()
// dfavgs.select($"Year",$"max(High)").show()

// What is the average Close for each Calender Month?
val df3 = df2.withColumn("Month", month(df("Date")))
val dfavgs = df3.groupBy("Month").mean()
dfavgs.select($"Month", $"avg(Close)").show()

// Create a DataFrame from Spark Session read csv
// Technically known as class Dataset
// val df2 = df.withColumn("Year",year(df("Date")))
// val dfmins = df2.groupBy("Year").min()
//
// dfmins.select($"Year", $"min(Close").show()
// Show Schema
// df.select(year(df("Date"))).show()

// Lot's of options here
// http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.functions$@add_months(startDate:org.apache.spark.sql.Column,numMonths:Int):org.apache.spark.sql.Column

// df.select(month(df("Date"))).show()
//
// df.select(year(df("Date"))).show()
//
// // Practical Example
// val df2 = df.withColumn("Year",year(df("Date")))
//
// // Mean per Year, notice large 2008 drop!
// val dfavgs = df2.groupBy("Year").mean()
// dfavgs.select($"Year",$"avg(Close)").show()
