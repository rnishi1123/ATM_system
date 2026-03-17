// ATM_Server.java
import java.io.*;
import java.net.*;

class FileRcvThread extends Thread {
    private Socket socket;
    byte[] rbuf = new byte[1024];
    byte[] sbuf = new byte[1024];

    public FileRcvThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        PassAccount account = null;
        int off = 0;
        String str;
        try {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            int len = in.read(rbuf);
            String file = new String(rbuf, 0, len);
            String res = "OKです";

            sbuf = res.getBytes();len = sbuf.length;
            out.write(sbuf, 0, len);

            System.out.println("\nクライアントとの接続ができました"); 


            len = in.read(rbuf);
            String fstr = new String(rbuf, 0, len);
            int f = Integer.parseInt(fstr);

            if (f == 1) { // 口座新規作成
                System.out.println("口座新規作成リクエスト受信\n");

                len = in.read(rbuf);
                str = new String(rbuf, 0, len);
                String[] line = str.split(",");
                account = new PassAccount(line[0], line[1], Integer.parseInt(line[2]), line[3]);

                // 新規口座をファイルに保存
                PassAccount.saveAccount(account, account.accountHolder);
                System.out.println("口座情報保存: " + account.accountHolder);

                String bname = "常翔銀行"; // 銀行名は固定 
                System.out.println("銀行名　　：" + bname);
                System.out.println("支店名　　：" + line[0]);
                System.out.println("名義人名　：" + line[1]);
                System.out.println("預金残高　：" + line[2] + "円");
                System.out.println("パスワード：" + line[3]); 
                System.out.println();

                String out_data = bname + "," + line[0] + "," + line[1] + "," + line[2];
                sbuf = out_data.getBytes();
                len = sbuf.length;
                out.write(sbuf, off, len); 

            } else if (f == 2) {
                System.out.println("既存口座ログインリクエスト受信\n"); 

                len = in.read(rbuf);
                str = new String(rbuf, 0, len);
                String[] logindata = str.split(","); 

                String accountHolder = logindata[0];
                String password = logindata[1];
                account = PassAccount.loadAccount(accountHolder);

                if (account == null) {
                    System.out.println("→ 指定された口座が存在しません: " + accountHolder); 
                    String err = "認証失敗";
                    sbuf = err.getBytes();
                    len = sbuf.length;
                    out.write(sbuf, 0, len);
                    return; 
                }

                if (!account.passcheck(password)) {
                    System.out.println("→ パスワードが間違っています: " + accountHolder); 
                    String err = "認証失敗";
                    sbuf = err.getBytes();
                    len = sbuf.length;
                    out.write(sbuf, 0, len);
                    return; 
                }

                System.out.println("→ ログイン成功: " + account.accountHolder); 
                String out_data = account.bankName + "," + account.branchName + "," + account.accountHolder + "," + account.getBalance();
                sbuf = out_data.getBytes();
                len = sbuf.length;
                out.write(sbuf, 0, len); 
            }

            do {
                len = in.read(rbuf);
                str = new String(rbuf, 0, len);
                String[] line2 = str.split(",");
                f = Integer.parseInt(line2[0]); 

                switch (f) {
                    case 1: // 預金
                        int deposit = Integer.parseInt(line2[1]);
                        System.out.println("→ 預金操作：" + deposit + "円 (口座: " + account.accountHolder + ")");
                        account.deposit(deposit);
                        // 預金後に口座の状態を保存
                        PassAccount.saveAccount(account, account.accountHolder);

                        str = String.valueOf(account.getBalance());
                        sbuf = str.getBytes();len = sbuf.length;
                        out.write(sbuf, 0, len);
                        break;
                    case 2: // 払い出し
                        int draw = Integer.parseInt(line2[1]);
                        int actualDraw = account.draw(draw);
                        System.out.println("→ 払い出し操作：要求=" + draw + "円 実行=" + actualDraw + "円 (口座: " + account.accountHolder + ")");
                        PassAccount.saveAccount(account, account.accountHolder);

                        str = actualDraw + "," + account.getBalance();
                        sbuf = str.getBytes();len = sbuf.length;
                        out.write(sbuf, 0, len);
                        break;
                    case 3: // 残高照会
                        System.out.println("→ 残高照会操作 (口座: " + account.accountHolder + ")");
                        int amount = account.getBalance();
                        str = String.valueOf(amount);

                        sbuf = str.getBytes();len = sbuf.length;
                        out.write(sbuf, 0, len);
                        break;
                    case 0: // 終了
                        System.out.println("→ 取引終了指示受信 (口座: " + account.accountHolder + ")");
                        break;
                }

            } while (f != 0);

            in.close();
            out.close();
            socket.close();
            System.out.println("クライアント接続終了");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class ATM_Server {
    public static void main(String[] args) {
        int PORT = 49152;
        System.out.println("サーバ起動"); // Server starting
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                Socket socket = server.accept();
                FileRcvThread th1 = new FileRcvThread(socket);
                th1.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}