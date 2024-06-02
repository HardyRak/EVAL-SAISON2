import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print(">");
        Scanner scan=new Scanner(System.in);
        String genere=scan.next();
        while(!genere.equals("quit")) {
            try{
                if(genere.equals("all")){
                    Function.generateAll();
                }else{
                    Function.generate(genere);
                }
            }catch(Exception e)
            {
                System.out.println(e.getMessage());
                continue;
            }
            System.out.println();
            System.out.print(">");
            genere=scan.next();
        }
        scan.close();
    }
}
