package kotlinDSL

import kotlinDSL.java.Client
import kotlinDSL.java.Company
import kotlinDSL.java.Twitter

fun main() {
    val builder = Client.builder()

    builder.myFirstName("Anton")
    builder.myLastName("Arhipov")

    val twitterBuilder = Twitter.builder()
    twitterBuilder.handle("@antonarhipov")
    builder.myTwitter(twitterBuilder.build())

    val companyBuilder = Company.builder()
    companyBuilder.myName("JetBrains")
    companyBuilder.myCity("Tallinn")
    builder.myCompany(companyBuilder.build())


    val client = builder.build()
    println("Created client is: $client")
}