import Model.SensoresModel;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Random;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

public class SensoresClient implements NodeConnectionListener {
    private SensoresModel informacao;
    private static String		gatewayIP   = "127.0.0.1";
    private static int		gatewayPort = 5500;
    private MrUdpNodeConnection	connection;
    public static void main(String[] args) throws InterruptedException {

        ArrayList<SensoresClient> Sensores = new ArrayList<SensoresClient>();
        SensoresClient sensor1 =new SensoresClient("350970001A",0);
        sensor1.informacao.setLatitude(-22.742f);
        sensor1.informacao.setLongitude(-45.595f);
        SensoresClient sensor2 =new SensoresClient("350970001GP",0);
        sensor2.informacao.setLatitude(-22.73986f);
        sensor2.informacao.setLongitude(-45.5985f);
        SensoresClient sensor3 =new SensoresClient("350970003A",0);
        sensor3.informacao.setLatitude(-22.759247f);
        sensor3.informacao.setLongitude(-45.611953f);
        SensoresClient sensor4 =new SensoresClient("350970003GP",0);
        sensor4.informacao.setLatitude(-22.75348f);
        sensor4.informacao.setLongitude(-45.61511f);
        SensoresClient sensor5 =new SensoresClient("350970003GS",1);
        sensor5.informacao.setLatitude(-22.73986f);
        sensor5.informacao.setLongitude(-45.5985f);
        //SensoresClient sensor6 =new SensoresClient("350970001A",0);

        Sensores.add(sensor1);
        Sensores.add(sensor2);
        Sensores.add(sensor3);
        Sensores.add(sensor4);
        Sensores.add(sensor5);
        //Sensores.add(sensor6);
        ArrayList<ArrayList<Map<Long,Float>>> dados = new ArrayList<ArrayList<Map<Long,Float>>>();
        for(int i=0;i<Sensores.size();i++){
            dados.add(Sensores.get(i).obter_dados(Sensores.get(i).informacao.getID(),Sensores.get(i).informacao.getTipoSensor()));
        }
        System.out.println(dados.get(0));
        int contador=0;
        while (true){
            System.out.println("-------------");
            for(int i=0;i<Sensores.size();i++){
                Map<Long,Float> dado = new HashMap<Long, Float>();
                long chave=0l;
                for (Long key : dados.get(i).get(contador).keySet()){
                    chave=key;
                }
                float valor=0f;
                for(float value:dados.get(i).get(contador).values()){
                    valor=value;
                }
                dado.put(chave,valor);
                Sensores.get(i).informacao.setDados(dado);
                Sensores.get(i).enviar();
            }

            contador++;
            TimeUnit.SECONDS.sleep(5);
        }
    }
    public void enviar(){
        ApplicationMessage message = new ApplicationMessage();
        message.setContentObject(informacao);
        try {
            connection.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public SensoresClient(String id, Integer tipo){
        informacao =new SensoresModel();
        informacao.setTipoSensor(tipo);
        informacao.setID(id);
        informacao.setMyUUID(UUID.randomUUID());
        InetSocketAddress address = new InetSocketAddress(gatewayIP, gatewayPort);
        try {
            connection = new MrUdpNodeConnection(informacao.getMyUUID());
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //public ArrayList<Float> obter_dados (String id){
    public ArrayList<Map<Long,Float>> obter_dados (String id, Integer tipo){
        String path = "C:\\Users\\carva\\IdeaProjects\\untitled\\src\\Dados\\";
        String arquivo = id;
        String line = "";
        ArrayList<Map<Long,Float>> retorno = new ArrayList<Map<Long,Float>>();
        try {
            if(tipo==0) {
                BufferedReader br = new BufferedReader(new FileReader(path + arquivo + ".csv"));

                while ((line = br.readLine()) != null) {

                    Map<Long, Float> dados = new HashMap<Long, Float>();
                    try {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Date data = sf.parse(Arrays.stream(line.split(";")).toArray()[6].toString());
                        float dado = Float.parseFloat(Arrays.stream(line.split(";")).toArray()[7].toString().replace(",", "."));


                        dados.put(data.getTime(), dado);
                        retorno.add(dados);
                    } catch (Exception e) {
                        System.out.println(e);
                    } finally {
                        continue;
                    }
                }
            }else{
                BufferedReader br = new BufferedReader(new FileReader(path + arquivo + ".csv"));
                long aux= 0l;
                float sum=0f;
                int contador=0;
                while ((line = br.readLine()) != null) {
                    Map<Long, Float> dados = new HashMap<Long, Float>();
                    try {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Date data = sf.parse(Arrays.stream(line.split(";")).toArray()[6].toString());
                        float dado = Float.parseFloat(Arrays.stream(line.split(";")).toArray()[8].toString().replace(",", "."));

                        if(retorno.size()==0){
                            aux=data.getTime();
                        }
                        if((aux-data.getTime())==0){
                            if(Arrays.stream(line.split(";")).toArray()[7].toString() !="chuva") {
                                sum+=(6-contador)*dado;
                                contador++;
                            }
                            if(contador==7){
                                dados.put(data.getTime(), sum/21);
                                retorno.add(dados);
                                contador=0;
                                sum=0;
                            }
                        }else{
                            aux=data.getTime();
                        }



                    } catch (Exception e) {

                        System.out.println(e);
                    } finally {
                        continue;
                    }
                }
            }
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        return retorno;
    }
    @Override
    public void connected(NodeConnection remoteCon) {
        ApplicationMessage message = new ApplicationMessage(); // vai enviar primeira coordenada
        message.setContentObject(informacao);
        try {													// envia primeiras informações e coordenadas
            connection.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        System.out.println(message);
    }
    // other methods
    @Override
    public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {}
    @Override
    public void disconnected(NodeConnection remoteCon) {}
    @Override
    public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {}
    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {}
}
