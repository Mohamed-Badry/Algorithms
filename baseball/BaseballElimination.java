import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;

public class BaseballElimination {
    private final int n;
    private final Map<String, Integer> teamMap;
    private final int[] w;
    private final int[] l;
    private final int[] r;
    private final int[][] g;
    private FordFulkerson[] maxFlowArray;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In file = new In(filename);

        n = file.readInt();
        teamMap = new HashMap<>();
        w = new int[n];
        l = new int[n];
        r = new int[n];
        g = new int[n][n];
        maxFlowArray = new FordFulkerson[n];

        for (int i = 0; i < n; i++) {

            String team = file.readString();
            teamMap.put(team, i);
            w[i] = file.readInt();
            l[i] = file.readInt();
            r[i] = file.readInt();

            for (int j = 0; j < n; j++) {
                g[i][j] = file.readInt();
            }
        }

        for (int teamID = 0; teamID < n; teamID++) {
            createFlowNetwork(teamID);
        }
    }

    // number of teams
    public int numberOfTeams() {
        return n;
    }

    // all teams
    public Iterable<String> teams() {
        return teamMap.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        validate(team);
        return w[teamMap.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        validate(team);
        return l[teamMap.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validate(team);
        return r[teamMap.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validate(team1);
        validate(team2);
        return g[teamMap.get(team1)][teamMap.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validate(team);
        int teamID = teamMap.get(team);

        // Trivial elimination
        for (String team2 : teams()) {
            if (!team.equals(team2) && w[teamID] + r[teamID] < w[teamMap.get(team2)]) {
                return true;
            }
        }

        // non-trivial elimination
        // check for edges pointing from s that are not full
        double maxFlowValue = maxFlowArray[teamID].value();
        int totalGames = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (i != teamID && j != teamID) {
                    totalGames += g[i][j];
                }
            }
        }
        return maxFlowValue < totalGames;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validate(team);
        if (!isEliminated(team)) {
            return null;
        }

        int teamID = teamMap.get(team);
        Bag<String> certificate = new Bag<String>();

        // trivial certificate
        for (String team2 : teams()) {
            if (!team.equals(team2) && w[teamID] + r[teamID] < w[teamMap.get(team2)]) {
                certificate.add(team2);
                return certificate;
            }
        }

        // non-trivial certificate
        FordFulkerson teamFlow = maxFlowArray[teamMap.get(team)];
        for (String team2 : teams()) {
            if (!team.equals(team2)) {
                int team2ID = teamMap.get(team2);
                int vertexID = n + team2ID - (team2ID > teamID ? 1 : 0);
                if (teamFlow.inCut(vertexID)) {
                    certificate.add(team2);
                }
            }
        }
        return certificate;

    }

    private void createFlowNetwork(int teamID) {
        int numberOfVertices = 2 + (n * (n - 1) / 2) + (n - 1);
        FlowNetwork teamNet = new FlowNetwork(numberOfVertices);

        int gameVertex = 1;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (i == teamID || j == teamID)
                    continue;
                int capacity = g[i][j];
                if (capacity > 0) {
                    teamNet.addEdge(new FlowEdge(0, gameVertex, capacity));
                    teamNet.addEdge(new FlowEdge(gameVertex, n + i - (i > teamID ? 1 : 0), Double.POSITIVE_INFINITY));
                    teamNet.addEdge(new FlowEdge(gameVertex, n + j - (j > teamID ? 1 : 0), Double.POSITIVE_INFINITY));
                }
                gameVertex++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (i == teamID)
                continue;
            int otherTeam = i < teamID ? i : i - 1;
            int capacity = w[teamID] + r[teamID] - w[i];
            if (capacity > 0) {
                teamNet.addEdge(new FlowEdge(n + otherTeam, numberOfVertices - 1, capacity));
            }
        }

        maxFlowArray[teamID] = new FordFulkerson(teamNet, 0, numberOfVertices - 1);
    }

    private void validate(String team) {
        if (team == null) {
            throw new IllegalArgumentException("team can't be null");
        }
        if (!teamMap.containsKey(team)) {
            throw new IllegalArgumentException("invalid team");
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }

    }

}
