package lab2;


import java.util.Arrays;

/**
 *   Ledger defines for each user the balance at a given time
     in the ledger model of bitcoins
     and contains methods for checking and updating the ledger
     including processing a transaction
 */

public class Ledger extends UserAmount{


    public boolean checkUserAmountDeductable(UserAmount userAmountCheck){
        for (String user : userAmountCheck.getUsers()){
            if (getBalance(user) < userAmountCheck.getBalance(user)) {return false;}
        }
	return true;
    }


    public boolean checkEntryListDeductable(EntryList txel){
        return checkUserAmountDeductable(new UserAmount(txel));
    }


    public void subtractEntryList(EntryList txel){
	    if (checkEntryListDeductable(txel)){
             UserAmount userAmount= new UserAmount(txel);
            for (String user : userAmount.getUsers()){
                subtractBalance(user,userAmount.getBalance(user));
            }
        }
    }


    public void addEntryList(EntryList txel) {
        UserAmount userAmount= new UserAmount(txel);
        for (String user : userAmount.getUsers()) {
            addBalance(user, userAmount.getBalance(user));
        }
    }


    public boolean checkTransactionValid(Transaction tx){
        return tx.checkTransactionAmountsValid() && checkEntryListDeductable(tx.toInputs());
    }



    public void processTransaction(Transaction tx){
	    if (checkTransactionValid(tx)){
            subtractEntryList(tx.toInputs());
            addEntryList(tx.toOutputs());
        }
    }

    public static void test(){
        System.out.println("--- STEP 1 ---");
        System.out.println("Creating a ledger consisting of 4 empty (0) accounts" );
        Ledger ledger = new Ledger();
        ledger.addAccount("Alice",0);
        ledger.addAccount("Bob",0);
        ledger.addAccount("Carol",0);
        ledger.addAccount("David",0);
        ledger.print();

        System.out.println("\n--- STEP 2 ---");
        System.out.println("Setting balances (Alice to 20, Bob to 15)" );
        ledger.setBalance("Alice",20);
        ledger.setBalance("Bob",15);
        ledger.print();

        System.out.println("\n--- STEP 3 ---");
        System.out.println("Adding 5 to Alice's balance and subtracting 5 from Bob's" );
        ledger.addBalance("Alice",5);
        ledger.subtractBalance("Bob",5);
        ledger.print();

        System.out.println("\n--- STEP 4 ---");
        System.out.println("Checking whether EntryList (txel1) deducting 15 from Alice's balance and 10 from Bob's is valid" );
        EntryList txel1 = new EntryList();
        txel1.addEntry("Alice",15);
        txel1.addEntry("Bob",10);
        System.out.println("Transaction Valid: " + ledger.checkEntryListDeductable(txel1));

        System.out.println("\n--- STEP 5 ---");
        System.out.println("Checking whether EntryList (txel2) deducting 30 (2x15) from Alice's balance and 5 from Bob's is valid" );
        EntryList txel2 = new EntryList();
        txel2.addEntry("Alice",15);
        txel2.addEntry("Alice",15);
        txel2.addEntry("Bob",5);
        System.out.println("Transaction Valid: " + ledger.checkEntryListDeductable(txel2));

        System.out.println("\n--- STEP 6 ---");
        System.out.println("Deducting txel1" );
        ledger.subtractEntryList(txel1);
        ledger.print();

        System.out.println("\n--- STEP 7 ---");
        System.out.println("Adding txel2" );
        ledger.addEntryList(txel2);
        ledger.print();

        System.out.println("\n--- STEP 8 ---");
        System.out.println("Checking whether the following transaction is valid: \n" +
                "Input:Alice: 45 | Output: Bob: 5 , Carol: 20" );
        Transaction tx1 = new Transaction(new EntryList("Alice",45),
                new EntryList("Bob",5,"Carol",20));
        System.out.println("Transaction Valid: " + ledger.checkTransactionValid(tx1));

        System.out.println("\n--- STEP 9 ---");
        System.out.println("Checking whether the following transaction is valid: \n" +
                "Input:Alice: 20 | Output: Bob: 5 , Carol: 20" );
        Transaction tx2 = new Transaction(new EntryList("Alice",20),
                new EntryList("Bob",5,"Carol",20));
        System.out.println("Transaction Valid: " + ledger.checkTransactionValid(tx2));

        System.out.println("\n--- STEP 10 ---");
        System.out.println("Checking whether the following transaction is valid: \n" +
                "Input:Alice: 25 | Output: Bob: 10 , Carol: 15 " );
        Transaction tx3 = new Transaction(new EntryList("Alice",25),
                new EntryList("Bob",10,"Carol",15));
        System.out.println("Transaction Valid: " + ledger.checkTransactionValid(tx3));

        System.out.println("\n--- STEP 11 ---");
        System.out.println("Processing the above transaction");
        ledger.processTransaction(tx3);
        ledger.print();

        System.out.println("\n--- STEP 12 ---");
        System.out.println("Processing the following transaction: \n" +
                "Input: Alice: 10 (5x2) | Output : Bob: 10");
        Transaction tx4 = new Transaction(new EntryList("Alice",5,"Alice",5),
                new EntryList("Bob",10));
        ledger.processTransaction(tx4);
        ledger.print();

    }


    public static void main(String[] args) {
	Ledger.test();	
    }
}