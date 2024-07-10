import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
   private final WordNet outcastNet;

   public Outcast(WordNet wordnet) // constructor takes a WordNet object
   {
      outcastNet = wordnet;
   }

   public String outcast(String[] nouns) // given an array of WordNet nouns, return an outcast
   {
      int maxDist = Integer.MIN_VALUE;
      String outcast = "";
      for (String x1 : nouns) {
         int x1Dist = 0;
         for (String x2 : nouns) {
            x1Dist += outcastNet.distance(x1, x2);
         }
         if (x1Dist > maxDist) {
            maxDist = x1Dist;
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
