// ATM_Client.java
import java.io.*;
import java.net.*;

public class ATM_Client {
    public static void main(String[] args) {
        int PORT = 49152;
        String IP = "127.0.0.1";
        byte[] rbuf = new byte[1024];
        byte[] sbuf = new byte[1024]; 

        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String str;
        int len;
        int off = 0;
        String money;
        String str1 = "OKです";

        try {
            InetAddress IPaddr = InetAddress.getByName(IP);
            Socket socket = new Socket(IPaddr, PORT);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            sbuf = str1.getBytes();
            len = sbuf.length;
            out.write(sbuf, off, len); 

            len = in.read(rbuf);
            String str2 = new String(rbuf, off, len);

            if (str1.equals(str2)) {
                System.out.println("サーバーとの接続が出来ました"); 
                System.out.println("接続先IPアドレス:" + IP);
            } else {
                System.out.println("No connection");
                socket.close();
                return; 
            }

            System.out.println("＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");
            System.out.println("常翔銀行へようこそ"); 
            System.out.println("＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊\n");

            System.out.println("操作方法を選択してください: 1.口座の開設　2.既存口座"); 
            int f = Integer.parseInt(br.readLine());
            sbuf = String.valueOf(f).getBytes();
            len = sbuf.length;
            out.write(sbuf, off, len); 

            if (f == 1) {//新規口座開設
                System.out.print("支店名　：");
                String bname = br.readLine();
                System.out.print("名義人名：");
                String yname = br.readLine();
                System.out.print("初期預金額(円):");
                String amount = br.readLine();
                System.out.print("パスワード:");
                String pass = br.readLine();

                String out_data = bname + "," + yname + "," + amount + "," + pass;
                sbuf = out_data.getBytes();
                len = sbuf.length;
                out.write(sbuf, off, len);

                len = in.read(rbuf);
                str = new String(rbuf, 0, len);
                
                String[] line = str.split(",");
                System.out.print("\n新規口座を開設しました\n\n"); 
                System.out.println("銀行名　　：" + line[0]); 
                System.out.println("支店名　　：" + line[1]); 
                System.out.println("名義人名　：" + line[2]); 
                System.out.println("預金残高　：" + line[3] + "円"); 
                
            } else if (f == 2) {//既存口座のログイン
                System.out.print("名義人名："); 
                String yname = br.readLine();
                System.out.print("パスワード:");
                String pass = br.readLine();

                String login_data = yname + "," + pass;
                sbuf = login_data.getBytes();
                len = sbuf.length;
                out.write(sbuf, off, len);

                len = in.read(rbuf);
                str = new String(rbuf, 0, len);

                if (str.equals("認証失敗")) {
                    System.out.println("ログインに失敗しました。名義人名またはパスワードが間違っています。");
                    socket.close(); 
                    return;
                } else {
                    String[] line = str.split(",");
                    System.out.println("\nログイン成功\n");
                    System.out.println("銀行名　　：" + line[0]);
                    System.out.println("支店名　　：" + line[1]);
                    System.out.println("名義人名　：" + line[2]); 
                    System.out.println("預金残高　：" + line[3] + "円");
                }
            }

            do {
                System.out.println("＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊\n");
                System.out.println("操作方法を選択してください: 1.預金　2.払い出し　3.残高照会　0.終了");
                System.out.print("->");
                f = Integer.parseInt(br.readLine());

                switch (f) {
                    case 1: // 預金
                        System.out.println("預金金額を入力して下さい:");
                        money = br.readLine();
                        money = f + "," + money;
                        sbuf = money.getBytes();
                        len = sbuf.length;
                        out.write(sbuf, off, len);

                        len = in.read(rbuf);
                        str = new String(rbuf, 0, len);
                        System.out.println("預金残高:" + str + "円");
                        break;

                    case 2: // 払い出し
                        System.out.println("払い出し金額を入力して下さい:");
                        money = br.readLine();
                        money = f + "," + money;
                        sbuf = money.getBytes();
                        len = sbuf.length;
                        out.write(sbuf, off, len);

                        len = in.read(rbuf);
                        str = new String(rbuf, 0, len);

                        String[] line2 = str.split(",");
                        System.out.println("払い出し金額:" + line2[0] + "円です      預金残高:" + line2[1] + "円");
                        break;

                    case 3: // 残高照会
                        sbuf = String.valueOf(f).getBytes();
                        len = sbuf.length;
                        out.write(sbuf, off, len);

                        len = in.read(rbuf);
                        str = new String(rbuf, 0, len);
                        System.out.println("預金残高:" + str + "円");
                        break;

                    case 0: // 終了
                        sbuf = String.valueOf(f).getBytes();
                        len = sbuf.length;
                        out.write(sbuf, off, len);
                        System.out.println("\n\nお取引を終了します。ご利用いただきありがとうございました。");
                        break;
                    default:
                        System.out.println("無効な操作です。");
                        break;
                }

            } while (f != 0);

            in.close();
            out.close();
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace(); 
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}