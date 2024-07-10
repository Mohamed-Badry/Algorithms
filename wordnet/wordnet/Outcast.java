import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
   private WordNet outcastNet;

   public Outcast(WordNet wordnet) // constructor takes a WordNet object
   {
      outcastNet = wordnet;
   }

   public String outcast(String[] nouns) // given an array of WordNet nouns, return an outcast
   {
      int d_max = Integer.MIN_VALUE;
      String outcast = "";
      for (String x1 : nouns) {
         int d_x1 = 0;
         for (String x2 : nouns) {
            d_x1 += outcastNet.distance(x1, x2);
         }
         if (d_x1 > d_max) {
            d_max = d_x1;
            outcast = x1;
         }
      }
      return outcast;
   }

   public static void main(String[] args) {
      WordNet wordnet = new WordNet(args[0], args[1]);
      Outcast outcast = new Outcast(wordnet);
      for (int t = 2; t < args.length; t++) {
         In in = new In(args[t]);
         String[] nouns = in.readAllStrings();
         StdOut.println(args[t] + ": " + outcast.outcast(nouns));
      }
   }
}
