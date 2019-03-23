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
Machine Setup:
----------------------------------------------------------
- JRE 8u201
- Java 1.8
- Setup system environment: JAVA_HOME to point to your Java SDK directory, or JRE_HOME to point at your Java JRE directory

----------------------------------------------------------
Build & Run Java application
----------------------------------------------------------
There is an executable JAR file supplied, but if you want to build it yourself...

1. Unzip the project-folder to a location.
2. Open your command-line/terminal and cd to the project's root folder "ShoppingBasket".
3. Run command to build: "......"
4. It should produce you an executable jar file, which you can now launch.

----------------------------------------------------------
Project Assumptions:
----------------------------------------------------------
- For the purpose of this project, will not use a database: will hard-code the Goods and Offers in the java file "RepositoryDb.java".
- There are unlimited goods and offers; no cap.
- Only working with GBP; will not implement mechanism to render other currencies.
- Displaying the numbers (major and minor ccy):
    - If number is 1 or above:  display in £##.## format (2 d.p)
    - If number is below 1:     display in #p format.

- The buying command:
    - Must start with "PriceBasket" followed by a list of the good's name, separated by spaces.
    - The list of goods can be empty (it will return 0p).
    - To purchase a good multiple times, simply enter its name again (e.g. to buy two apples: "PriceBasket Apples Apples")
    - All inputs are not case-sensitive.
    - Number of spaces inbetween the goods do not matter.
    - Hard rejection: if there are any unknown/invalid/typo of the items detected: flag error to end-user.

    ---------------------------------
    Case one: goods with offer
    Input:
      PriceBasket Apples Milk Bread

    Output:
      Subtotal: £3.10
      Apples 10% off: -10p
      Total: £3.00
    ---------------------------------
    Case two: goods with no offer
    Input:
      PriceBasket Milk

    Output:
      Subtotal: £1.30
      (No offers available)
      Total price: £1.30
    ---------------------------------
    Case three: no goods
    Input:
      PriceBasket 

    Output:
      Subtotal: 0p
      (No offers available)
      Total price: 0p
    ---------------------------------
    Case four: invalid good
    Input:
      PriceBasket Apples Bob Jim

    Output:
      Error: Detected unknown goods - Bob Jim      
    ---------------------------------

TODO:
- Test case: all possibilities.
- handle different input-commands.
- fastest way to trim whitespace string.
- need .bash script to generate files: https://www.mkyong.com/java/how-to-make-an-executable-jar-file/ 
