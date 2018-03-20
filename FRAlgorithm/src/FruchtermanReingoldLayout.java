
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by XiaoXiao on 2018/3/6.
 */
public class FruchtermanReingoldLayout {

    private int W =500; // 画布的宽度
    private int L =500 ;  //画布的长度
    private double temperature = 200; //模拟退火初始温度
    private int maxIter = 50; //算法迭代次数
    private double attractiveForce = 2000;  // 节点间引力系数
    private double repulsiveForce=30; //节点间斥力系数
    private List<String>  V=null;
    private List<String[]> E=null;
    private HashMap<String,double[]> positions=null;
    private HashMap<String,double[]> forces=null;
    //private String file="D:\\Work\\trivials\\FRAlgorithm\\data\\arbol.txt";



    /**
     * init  FruchtermanReingoldLayout
     * @param W   the wide of graph
     * @param L   the length of graph
     * @param temperature   define the initial value of temperature
     * @param maxIter  the max iterator of the arig
     * @param attractiveForce   define the initial value of attractiveForce
     * @param repulsiveForce    define the initial value of repulsiveForce
     */
    //指定参数值，不指定选默认值
    public void init(int W, int L, double temperature,int maxIter, double attractiveForce, double repulsiveForce ){
        this.W = W;
        this.L = L;
        this.temperature=temperature;
        this.maxIter = maxIter;
        this.attractiveForce=attractiveForce;
        this.repulsiveForce=repulsiveForce;
    }

    //初始化各个顶点的位置
    private void initVertices() {

        positions = new HashMap<>();
        Random random = new Random();
        for (int i = 0; i < V.size(); i++) {
            double[] p=new double[2];
            p[0] =-W/2+W*random.nextDouble();
            p[1]=-L/2+L*random.nextDouble();
            positions.put(V.get(i),p);
        }
    }

    //计算引力
    private double calculateAttractionForce(double value){
        return Math.pow(value,2)/attractiveForce;
    }

    //计算斥力
    private double calculateRepulsionForce(double value){
        return Math.pow(repulsiveForce,2)/value;
    }

    private double[] sub(double[] x,double[] y){
        int n=x.length;
        double[] d=new double[n];
        for(int i=0;i<n;i++){
            d[i]=x[i]-y[i];
        }
        return d;
    }

    private double[] sum(double[] x,double[] y){
        int n=x.length;
        double[] d=new double[n];
        for(int i=0;i<n;i++){
            d[i]=x[i]+y[i];
        }
        return d;
    }

    private double[] mul(double[] x,double m){
        int n=x.length;
        double[] d =new double[n];
        for(int i=0;i<n;i++){
            d[i]=x[i]*m;
        }
        return d;
    }

    private double[] div(double[] x,double v){
        int n=x.length;
        double[] d =new double[n];
        for(int i=0;i<n;i++){
            d[i]=x[i]/v;
        }
        return d;
    }



    private double cool(){
       return temperature*0.95;
    }

    //FF算法步骤
    private void frLayout(){
        //初始化各点的作用力
        forces=new HashMap<>();
        for(int i=0;i<V.size();i++){
            double[] f={0,0};
            forces.put(V.get(i),f);
        }

        //计算各点之间的斥力
        String v1=null;
        String v2=null;
        double[] delta=new double[2];
        double mod_delta=0;
        for(int i=0;i<V.size();i++){
            v1=V.get(i);
            for (int j=i+1;j<V.size();j++){
                v2=V.get(j);
                delta=sub(positions.get(v1),positions.get(v2));
                mod_delta=Math.max((Math.sqrt(Math.pow(delta[0],2)+Math.pow(delta[1],2))),0.02);
                 forces.put(v1,sum(forces.get(v1),mul(div(delta, mod_delta), calculateRepulsionForce(mod_delta))));
                 forces.put(v2,sub(forces.get(v2),mul(div(delta, mod_delta), calculateRepulsionForce(mod_delta))));
            }
        }

        //计算各边之间的引力
        for(int i=0;i<E.size();i++){
            v1=E.get(i)[0];
            v2=E.get(i)[1];
            delta=sub(positions.get(v1),positions.get(v2));
            mod_delta=Math.max((Math.sqrt(Math.pow(delta[0],2)+Math.pow(delta[1],2))),0.02);
            forces.put(v1,sub(forces.get(v1),mul(div(delta, mod_delta), calculateAttractionForce(mod_delta))));
            forces.put(v2,sum(forces.get(v2),mul(div(delta, mod_delta), calculateAttractionForce(mod_delta))));
        }

        //更新各顶点的位置
        double[] disp=new double[2];
        double modDisp=0;
        String v=null;
        for(int i=0;i<V.size();i++){
            v=V.get(i);
            disp=forces.get(v);
            modDisp=Math.max((Math.sqrt(Math.pow(disp[0],2)+Math.pow(disp[1],2))),0.02);
            positions.put(v,sum(positions.get(v), mul(
                    div(disp, modDisp), Math.min(modDisp,temperature))));

        }

        temperature=cool();
        /*
        Iterator iter = positions.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry=(Map.Entry)iter.next();
            Object key=entry.getKey();
            //Object value=entry.getValue();
            System.out.println(key+":"+positions.get(key)[0]+","+positions.get(key)[1]+"]");
        }
        System.out.println("********"+temperature+"****************");
        System.out.println("--------------分割线-------------");*/
    }

    private void run(){
        for(int i=0;i<maxIter;i++){
            frLayout();
        }
    }

    private void writeResult(String file){
        try {
            PrintWriter printWriter=new PrintWriter(new FileWriter(file));
            Iterator iter = positions.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                //Object value=entry.getValue();
                //System.out.println(key+":"+positions.get(key)[0]+","+positions.get(key)[1]+"]");
                String x = String.valueOf(positions.get(key)[0]);
                String y = String.valueOf(positions.get(key)[1]);
                String res = key+"\t"+x + "\t" + y;
                printWriter.println(res);
            }
            printWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void readData(String  file){
        try {
        String str=null;
        String tmpV=null;
        int n=0;  //顶点数目
            V=new ArrayList<String>();//顶点列表
            E=new ArrayList<String[]>();//边列表
        FileInputStream input=new FileInputStream(file);
        InputStreamReader inputStreamReader=new InputStreamReader(input);
        BufferedReader br=new BufferedReader(inputStreamReader);
            n=Integer.parseInt(br.readLine());

            for (int i=0;i<n;i++){
                tmpV=br.readLine().trim();
                //System.out.println(tmpV);
                V.add(tmpV);
            }
            String[] tmpE=null;
            while((str=br.readLine())!=null){
               tmpE=str.split(" ");
                //System.out.println(tmpE[0]+","+tmpE[1]);
                E.add(tmpE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception{
        FruchtermanReingoldLayout frl=new FruchtermanReingoldLayout();
        frl.readData(args[0]);//读取输入顶点和边
        frl.initVertices(); //初始化各顶点位置
        //frl.init(500,500,200,100,2000,100); //初始化参数，如果不调用参数初始化函数，则采用默认值
        frl.run(); //运行FruchtermanReingold算法
        frl.writeResult(args[1]); //结果写入
    }
}
