# mobiquity_challenge
Mobiquity 2021 - Packing Challenge - Leonardo Oyharzabal


# Generate library

Step1:
Clone this repository using the command: git clone https://github.com/leonardooyharzabal/mobiquity_challenge

Step2:
To generate the library is necessary to install maven https://maven.apache.org/install.html.

Step3:
Get into the project folder: cd mobiquity_challenge

Step4:
Build the library, run the command: mvn package. 

Step5:
The library package_challenge-1.0.jar will be generated in the target folder. Include into your project and start using it!


# Find best package solution approach

The main idea of the algorithm is to divide the problem into smaller cases. For this, given a candidate item, a recursive method calculates if the candidate item should be added or not to the package based on solving this problem for the rest of the package items. 

Case 1- If the item is added, then the problem translates into finding the best solution for the rest of the list with a new capacity (total_capacity - item_weight)

Case 2- If the item is not added, then the problem translates into finding the best solution for the rest of the list with the same capacity.

After these two scenarios are considered, the resulting solution is the best option between cases 1 and 2.

In each step the algorithm removes the item that is being processed from the list, so it is ensured that the base case is achieved which is when the list of items is empty.
