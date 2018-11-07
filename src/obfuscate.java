import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class obfuscate {
    private ArrayList<String> fields = new ArrayList();
    private Boolean isBuild;
    private String HTML = new String();
    private String ClearPW = new String();
    private String DirtyPW = new String();
    private static final Random random = new java.security.SecureRandom();
    private static char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890ABCEDFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public void obfuscate(String password)
    {
        obfuscate(password, 8 ,8, 20);
        //Parameterfree returns standard Length of 8
    }

    public void obfuscate(String password, int minLength, int maxLength)
    {
        //Default Text Block Size is 20 Passwords
        obfuscate(password, minLength, maxLength, 20);
    }
    public void obfuscate(String password, int minLength, int maxLength, int Blocks)
    {
        //full function, hides the password in one of 20 blocks among random named
        isBuild = false;
        BuildBlock(password, minLength, maxLength, Blocks);
    }
    public void obfuscate()
    {
        obfuscate("password", 8 ,8, 20);
        //basic Constructor that does nothing
        //for now
    }
    public String BuildBlock(String password, int minLength, int maxLength, int Blocks) //returns a Status
    {
        String Status = null;
        ClearPW = password;
        boolean usePW = false;
        if( maxLength < minLength ) { int swa = minLength; maxLength = minLength; minLength = swa; }
        if( minLength < 8 ) { System.err.println("Minimum Password Length should never be less than 8"); }
        if( minLength < 0 ) { minLength = 1; }
        if( maxLength > 128 ) { System.err.println("Maximum Length seems excessive"); }
        if( maxLength > 512 ) { System.out.println("Maximum Length was set to 512"); }
        //if( password.length()*2 > maxLength ) { System.out.println("Maximum Length seems to exceed Password Length by too much"); }
        //if( password.length()*0.5 < minLength ) { System.out.println("Minimum Length seems to exceed Password Length by too much"); }

        //actual Working Process, all those Constructor functions seem unnecessary
        int iterations = 0;
        while( usePW == false)
        {
            iterations++;
            //the actual program inserts the password randomly, if this does not happen we try again
            fields.clear();
            for( int i = 0; i < Blocks; i++)
            {
                if( usePW == false && (int)Math.abs(Math.random()*Blocks) == Math.abs(Blocks/2) ) //it doesnt even matter what number i choose
                {
                    usePW = true;
                    Status = "Password insertet, Iteration "+iterations+", Position "+i;
                    int moreNoise = (int)Math.abs(password.length()*0.2)+1; //randomly adds a few letters to the password start and end
                    String tempPW = pw(random.nextInt(moreNoise)) + password + pw(random.nextInt(moreNoise));
                    fields.add(tempPW);
                    DirtyPW = tempPW;
                }
                else
                {
                    if( maxLength == minLength ) fields.add(pw(minLength));
                    else fields.add(pw(minLength+ random.nextInt(maxLength-minLength)));
                }
            }
        }
        return Status;
    }

    public String toString()
    {
        String backString = null;
        for( String line : fields)
        {
            backString = backString + line;
        }
        return backString;
    }

    public String toHtml(PWColor colors)
    {
        return toHtml( colors, lang.STR_RANDOMCOLOR);
    }

    public String toHtml(PWColor colors, String colorpref)
    {
        HTML = "";
        //this also just returns a String, but with HTML Formating!
        if( colors.PWColors.size() == 0 ) colors.loadDummyData();
        ArrayList<String> translate = new ArrayList<String>();
        HTML+= "<html><body class=\"main\"><style>.main{width: 600px;background:#707070;word-wrap: break-word;font-family: Courier New,Courier,Lucida Sans Typewriter,Lucida Typewriter,monospace;font-weight:700;}";// i love hard coded HTML in the morning
        colors.PWColors.forEach((k,v) ->
            {
                k = k.replaceAll(" ", "_");
                translate.add(k);
                //System.out.println(k + ":" + v);
                HTML+= ".css_"+k+" { color: #"+v+"; }";
            });
        HTML+= "</style>";
        for( String line : fields)
        {
            if( colorpref != lang.STR_RANDOMCOLOR && line == DirtyPW ) //so when its the actual password i wish to encrypt
            {
                HTML+= "<span class=\"css_"+colorpref.replaceAll(" ", "_")+"\">"+line+"</span>";
            }
            else
                HTML+= "<span class=\"css_"+translate.get(random.nextInt(translate.size()))+"\">"+line+"</span>";
        }
        HTML+= "</body></html>";
        return HTML;
    }

    public String getHTML()
    {
        //there is not setter for this, html should never be set by anyone except the building class
        //it would also be nonsensical to directly access the string
        //thinking about it, this function is quite nonsensical, if called before building it wont return anything
        //so calling the toHTML does basically the same. theoretically one could shave off some milliseconds by not rebuilding
        //the html each time, but this sounds like a very limited use case
        return HTML;
    }

    public static String generatePW(int minLength, int maxLength)
    {
        //i see no downside in declaring this publicly avaible
        if( maxLength < minLength ) { int swa = minLength; maxLength = minLength; minLength = swa; }
        int rotations = minLength + (int)Math.abs((maxLength+1-minLength)*Math.random());
        //+1, this means it might reach maxLength 1 very rarely, but otherwise it will almost never reach maxLength
        //System.out.println("Min/Max" + minLength + "/" + maxLength + " -> " + rotations);

        /*i can spontanously think of two ways to get a random letter from a set i define, one is to
        pre create a list and then select a random result, or do it in two iterations in which
        we select a type first and then a random number for the char in the second*/

        ArrayList<Character> charlist = new ArrayList();
        //26 letters lowercase, 26 letters uppercase, 10 letters in Numbers
        for( int i = 48; i <= 57; i++  ) { charlist.add((char)i); } //Numbers
        for( int i = 65; i <= 90; i++  ) { charlist.add((char)i); } //UpperCase
        for( int i = 97; i <= 122; i++ ) { charlist.add((char)i); } //Lower Case

        String randompw = new String("");
        for ( int i = 0; i < rotations; i++ )
        {
            //Letter, Capital Letter or Number
            randompw = randompw + charlist.get((int)Math.abs(charlist.size()*Math.random())).toString();
        }
        return randompw;
    }
    public static String pw(int len) {
        return new String(IntStream.range(0,len).map(i -> chars[random.nextInt(chars.length)]).toArray(),0,len);
    }

    public static String wordPw(int minLen)
            //minLen = minimum Number of characters for the words
    {
        return "";
    }

    public static void testRAS(String[] args) throws IOException {
        int sampleSize = 3000;
        int fileSize = 50000;
        int[] linesNumber = new int[sampleSize];
        Random r = new Random();
        for (int i = 0; i < linesNumber.length; i++) {
            linesNumber[i] = r.nextInt(fileSize);

        }
        List<Integer> list = Arrays.stream(linesNumber).boxed().collect(Collectors.toList());
        Collections.sort(list);

        BufferedWriter outputWriter = Files.newBufferedWriter(Paths.get("localOutput/output.txt"));
        long t1 = System.currentTimeMillis();
        try(BufferedReader reader = new BufferedReader(new FileReader("extremely large file.txt")))
        {
            int index = 0;//keep track of what item we're on in the list
            int currentIndex = 0;//keep track of what line we're on in the input file
            while(index < sampleSize)//while we still haven't finished the list
            {
                if(currentIndex == list.get(index))//if we reach a line
                {
                    outputWriter.write(reader.readLine());
                    outputWriter.write("\n");//readLine doesn't include the newline characters
                    while(index < sampleSize && list.get(index) <= currentIndex)//have to put this here in case of duplicates in the list
                        index++;
                }
                else
                    reader.readLine();//readLine is dang fast. There may be faster ways to skip a line, but this is still plenty fast.
                currentIndex++;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        outputWriter.close();
        System.out.println(String.format("Took %d milliseconds", System.currentTimeMillis() - t1));
    }
    public static String l33t(String str)
    {
        str = str.replaceAll("e|E", "3");
        str = str.replaceAll("a|A", "4");
        str = str.replaceAll("i|I", "1");
        str = str.replaceAll("o|O", "0");
        return str;
    }
}
