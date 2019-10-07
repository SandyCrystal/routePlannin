import java.io.*;
import java.util.*;

public class Solve {

    class Line {
        int id;
        String name;
        List<String> stations = new ArrayList<String>();
    }

    class Station {
        String name;
        boolean visited;
        String preStation;
        List<String> lineNow = new ArrayList<String>();
        List<Station> nextStation = new ArrayList<Station>();
    }

    static List<Line> lines = new ArrayList<Line>();
    static List<Station> stations = new ArrayList<Station>();
    static HashMap<String, Station> map = new HashMap<>();

    public void getSubwayMessage(String fileIn) {
        try {
            int cnt = 1;
            String path = fileIn;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"UTF-8"));
            String getLine = null;

            while ((getLine = bufferedReader.readLine()) != null) {
                Line line = new Line();
                String[] list = getLine.split(" ");
                line.id = cnt;
                line.name = list[0];

                for(int i = 1; i < list.length-1; i++) {
                    Station station1 = new Station();
                    Station station2 = new Station();
                    if(map.containsKey(list[i])) {
                        station1 = map.get(list[i]);
                        map.remove(list[i]);
                    } else {
                        station1.name = list[i];
                        station1.visited = false;
                    }
                    if(map.containsKey(list[i+1])) {
                        station2 = map.get(list[i+1]);
                        map.remove(list[i+1]);
                    } else {
                        station2.name = list[i+1];
                        station2.visited = false;
                    }
                    if(!station1.lineNow.contains(line.name)) {
                        station1.lineNow.add(line.name);
                    }
                    if(!station2.lineNow.contains(line.name)) {
                        station2.lineNow.add(line.name);
                    }
                    if(!station1.nextStation.contains(station2)) {
                        station1.nextStation.add(station2);
                    }
                    if(!station2.nextStation.contains(station1)) {
                        station2.nextStation.add(station1);
                    }
                    station1.preStation = station1.name;
                    station2.preStation = station2.name;
//                    System.out.println(list[i] + "   " + station1.name);
//                    System.out.println("-----------------");
                    map.put(list[i], station1);
                    map.put(list[i+1], station2);

                    if (!line.stations.contains(station1.name)) {
                        line.stations.add(station1.name);
                    }
                    if (!line.stations.contains(station2.name)) {
                        line.stations.add(station2.name);
                    }
                }

                lines.add(line);
                cnt++;
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.err.println("read errors: " + e);
        }
        return ;
    }

    public void getStationByLine(String name, String fileOut) {
        List<String> ans = new ArrayList<String>();
        for (Line line : lines) {
            if (line.name.equals(name)) {
                ans = line.stations;
                break;
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(fileOut);
            int index = 0;
            for (String station : ans) {
                if (index == 0) {
                    fileWriter.write(station);
                    index = 1;
                } else {
                    fileWriter.write("->" + station);
                }
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println();
        return;
    }

    public void BFS(String st, String ed) {
        for (Map.Entry<String, Station> entry : map.entrySet()) {
            entry.getValue().visited = false;
        }
        Queue<String> queue = new LinkedList<>();
        queue.add(st);
        while(!queue.isEmpty()) {
            String now = queue.poll();
            map.get(now).visited = true;

            if(now.equals(ed)) {
                break;
            }
            for(Station station : map.get(now).nextStation) {
                if(map.get(station.name).visited==false) {
                    map.get(station.name).preStation = now;
                    queue.add(station.name);
                }
            }
        }
    }

    public void printPath(String st, String ed, String fileOut) {
        List<String> list = new ArrayList<>();
        String now = ed;
        int flag = 0;
        String preLine = "";

        while(!now.equals(st)) {
            list.add(now);
            now = map.get(now).preStation;
        }
        Collections.reverse(list);

        try {
            FileWriter fileWriter = new FileWriter(fileOut);
            fileWriter.write(list.size() + "\r\n");
            fileWriter.write(st);
            for(int i = 0; i < list.size(); i++) {
                flag = 0;
                if(map.get(list.get(i)).lineNow.size()==1) {
                    fileWriter.write("->" + list.get(i));
                } else {
                    int j;
                    for(j = i+1; j < list.size(); j++) {
                        if(map.get(list.get(j)).lineNow.size()==1) {
                            if(!map.get(list.get(i)).lineNow.get(0).contains(map.get(list.get(j)).lineNow.get(0))) {
                                if(preLine.equals(map.get(list.get(j)).lineNow.get(0))) {
                                    fileWriter.write("->" + list.get(i));
                                } else {
                                    fileWriter.write("\r\n");
                                    fileWriter.write(map.get(list.get(j)).lineNow.get(0) + "\r\n");
                                    preLine = map.get(list.get(j)).lineNow.get(0);
                                    fileWriter.write(list.get(i));
                                }
                            }
                            break;
                        }
                    }
                }
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Solve solve = new Solve();
        String fileIn = "";
        String fileOut = "";
        String line = "";
        String start = "";
        String end = "";

        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-map")) {
                i++;
                fileIn = args[i];
            }
            if(args[i].equals("-a")) {
                i++;
                line = args[i];
            }
            if(args[i].equals("-o")) {
                i++;
                fileOut = args[i];
            }
            if(args[i].equals("-b")) {
                i++;
                start = args[i];
                i++;
                end = args[i];
            }
        }

        if(args.length==2) {
            solve.getSubwayMessage(fileIn);
            System.out.println("读入的地铁信息如下:");
            for(Line line1 : lines) {
                System.out.print(line1.name + ":");
                for(String s : line1.stations) {
                    System.out.print(" " + s);
                }
                System.out.println();
            }
        } else if(args.length==6) {
            solve.getSubwayMessage(fileIn);
            int flag = 0;
            for(Line line1 : lines) {
                if(line1.name.equals(line)) {
                    flag = 1;
                }
            }
            if(flag==1) {
                solve.getStationByLine(line, fileOut);
                System.out.println("Successfully output all site results on the route to " + fileOut);
            } else {
                System.out.println("北京地铁线路中不存在'" + line + "'线路");
            }

        } else if(args.length==7) {
            solve.getSubwayMessage(fileIn);
            int flag1 = 0;
            int flag2 = 0;
            for(Line line1 : lines) {
                if (line1.stations.contains(start)) {
                    flag1 = 1;
                }
            }
            for(Line line1 : lines) {
                if (line1.stations.contains(end)) {
                    flag2 = 1;
                }
            }
            if(flag1==1 && flag2==1) {
                solve.BFS(start, end);
                solve.printPath(start, end, fileOut);
            }
            if(flag1==0) {
                System.out.println("北京地铁线路中不存在'" + start + "'站点");
            }
            if(flag2==0) {
                System.out.println("北京地铁线路中不存在'" + end + "'站点");
            }
        }
    }
}