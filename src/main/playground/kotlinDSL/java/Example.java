package kotlinDSL.java;

public class Example {
    public static void main(String[] args) {
        final Client.ClientBuilder builder = Client.builder();

        builder.myFirstName("Anton");
        builder.myLastName("Arhipov");

        final Twitter.TwitterBuilder twitterBuilder = Twitter.builder();
        twitterBuilder.handle("@antonarhipov");
        builder.myTwitter(twitterBuilder.build());

        final Company.CompanyBuilder companyBuilder = Company.builder();
        companyBuilder.myName("JetBrains");
        companyBuilder.myCity("Tallinn");
        builder.myCompany(companyBuilder.build());


        final Client client = builder.build();
        System.out.println("Created client is: " + client);
    }
}