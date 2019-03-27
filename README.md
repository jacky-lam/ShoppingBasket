----------------------------------------------------------------------------------------
Coding Test - Shopping Basket

----------------------------------------------------------------------------------------
Project background
----------------------------------------------------------------------------------------
Write a program driven by unit tests that can price a basket of goods taking into account some special
offers.

The goods that can be purchased, together with their normal prices are:
- Soup – 65p per tin
- Bread – 80p per loaf
- Milk – £1.30 per bottle
- Apples – £1.00 per bag

Current special offers:
- Apples have a 10% discount off their normal price this week
- Buy 2 tins of soup and get a loaf of bread for half price

The program should accept a list of items in the basket and output the subtotal, the special offer discounts
and the final price.
Input should be via the command line in the form PriceBasket item1 item2 item3 ...

Input:
    PriceBasket Apples Milk Bread

Output should be to the console, for example:
    Subtotal: £3.10
    Apples 10% off: -10p
    Total: £3.00

If no special offers are applicable the code should output:
    Subtotal: £1.30
    (No offers available)
    Total price: £1.30
    

----------------------------------------------------------
Machine Setup required:
----------------------------------------------------------
- JRE 8u201
- Java 1.8 sdk
- Setup system environment for Windows:
    - JAVA_HOME to point to your Java SDK directory, or JRE_HOME to point at your Java JRE directory
    - PATH variable includes directory to your executable javac file (for building using javac command)
- Have Maven installed (will be building in command line via Maven).

----------------------------------------------------------
Build & Run Java application
----------------------------------------------------------
There is an executable JAR file supplied, but if you want to build it yourself...

1. Unzip the project-folder to a location/Downlaod from https://github.com/jacky-lam/ShoppingBasket
2. Open your command-line/terminal and cd to the project's root folder "ShoppingBasket".
3. Run command to build via Maven:
    A. mvn clean install
    B. mvn dependency:copy-dependencies
    C. cd target
        - you should hopefully see a "ShoppingBasket-1.0-SNAPSHOT.jar" and folder "dependency"
    D. run java:
        - Windows: java -cp ShoppingBasket-1.0-SNAPSHOT.jar;dependency/*;. com.app.ShoppingBasketMain
        - Linux/Mac (should in theory be): java -cp ShoppingBasket-1.0-SNAPSHOT.jar:dependency/*;. com.app.ShoppingBasketMain

*Debugger logs are turned off. To turn it on, just change the log4j.properties in the resource folder before building.
    From:   log4j.rootLogger=OFF, stdout
    To:     log4j.rootLogger=DEBUG, stdout

----------------------------------------------------------
Project Assumptions:
----------------------------------------------------------
- This is just a standard Java application that runs on console.
- For the purpose of this project, will not use a database: Goods and Offers are hardcoded in their DAO classes.
- All the date-time reference in system is universal & assumes one consistent timezone (using LocalDateTime).
- There are unlimited goods and offers; no cap.
- On the output, the amount shown next to each offer represents the total amount discounted from that offer.
    - e.g. if we had 2 apples in the basket; the output would show -20p (10% off per apple, 10% x 2 x 1.00 = 0.2)

- Prices of the goods are temporal.
- Only working with GBP; will not implement mechanism for currency conversion.
- Money formatting:
    - If ABS(number) is 1 or above:  display in £##.## format (always 2 d.p)
    - If ABS(number) is below 1:     display in #p format (no leading 0s)

- Offer structure:
    1. There are start-end date.
    2. There are conditions that specify the amount of goods required (criteria goods).
    3. There are specified goods that will be discounted (discounted goods).
    4. Point 1 and 2 must be fulfilled in the Basket for the offer to be applied.
    5. Discount can be in various forms. I have implemented "percentage off discounted goods" for now.
        - There could have been other ones like "amount taken off" or "the new price for set".
    6. Some offers may allow you to give discounts towards the criteria goods, some may not.
        - E.g. Include criteria: "All apples are 50%" or "Buy two breads, get one apple 50% as well".
        - E.g. Exclude criteria: "Every one apple you buy, you get two apples free" or "One one soup, get the other free".
        - There is a flag in the Offer class for this.
    7. Once the items in the basket have been discounted/matched in criteria, they cannot be used for another offer.

    Rule: "For the number of X we find in basket, that is the maximum number of times we can apply the discount towards the Y".
    X = number of times the basket has matched its criteria goods.
    Y = the set of discounted items.

- Applying the Offers to the basket:
    - It was not mentioned if there was ranking involved for executing the Offers.
    - Therefore, I have prioritised the offers based on their starting-date (Offers with recent start-dates have higher priority).

- The terminate command: Exit
    - Not case-sensitive.

- The buying command:
    - Must start with "PriceBasket" followed by a list of the good's name, separated by at least one space.
    - The list of goods can be empty (this will return 0p).
    - All inputs are not case-sensitive.
    - Hard rejection: if there are any unknown/invalid item name in the list: flag error to end-user.

---------------------------------
Input Examples:
---------------------------------

- Case one: goods with offer
    - Input:
        PriceBasket Apples Milk Bread
    - Output:
        Subtotal: £3.10
        Apples 10% off: -10p
        Total: £3.00

- Case two: goods with no offer
    - Input:
        PriceBasket Milk
    - Output:
        Subtotal: £1.30
        (No offers available)
        Total price: £1.30

- Case three: no goods
    - Input:
        PriceBasket 
    - Output:
        Subtotal: 0p
        (No offers available)
        Total price: 0p

---------------------------------
Potential improvements
---------------------------------
- Offers that my code did not cater for:
    - For a given order, it assumes you are applying the same discount towards all the goods.
        - There isn't a way to apply discount percentage for different items in the basket.
    - Strict on the amount of discounted Goods in basket:
        - e.g. It cannot implement this offer: "Buy one bread, and get up to 5 apples for free".
        - What happens now is "Buy one bread, and get exactly 5 apples free if you have all 5 in basket"
    - Not best price:
        - It's priority is based on start-date. It does not calculate all the other combinations to get best price.

- Should have used BigDecimal to give better precision when manipulating numbers.
    - Java has precision issues when manipulating ints/doubles.

- Only made immutable gets on Basket class. Could have done it on some other classes where applicable.

- Allow USD conversion.

- Provide other discount types, i.e. meal deal idea where you buy 3 items and it's one fixed price.
