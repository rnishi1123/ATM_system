// PassAccount.java
import java.io.Serializable;
import java.io.*;

class Account implements Serializable {

    public final String bankName; 
    public final String branchName;
    public final String accountHolder;
    private int amount;

    public Account(String bankName, String branchName, String accountHolder, int initialValue) {
        this.bankName = bankName;
        this.branchName = branchName;
        this.accountHolder = accountHolder;
        amount = initialValue;
    }

    public void deposit(int depositValue) {
        amount = amount + depositValue;
    }

    public int draw(int drawValue) {
        if (amount < drawValue) {
            drawValue = amount; 
            amount = 0;
        } else {
            amount = amount - drawValue;
        }
        return drawValue;
    }

    public int getBalance() {
        return amount;
    }

}

public class PassAccount extends Account implements Serializable {//継承
    private String password;

    public PassAccount(String branchName, String accountHolder, int initialValue, String password) {
        super("常翔銀行", branchName, accountHolder, initialValue); 
        this.password = password;
    }

    public boolean passcheck(String pass) {
        return this.password.equals(pass);
    }

    
    public static void saveAccount(PassAccount account, String file) {//アカウント内容保存
        try{
            FileOutputStream outFile = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(outFile);
            out.writeObject(account);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PassAccount loadAccount(String file) {//アカウント情報の取得
        try{
            FileInputStream inFile = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(inFile);
            PassAccount inAccount = (PassAccount) in.readObject();
            return inAccount;
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}