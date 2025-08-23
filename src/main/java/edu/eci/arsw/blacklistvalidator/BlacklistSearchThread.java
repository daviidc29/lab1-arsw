package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class BlacklistSearchThread extends Thread{
    private final String ippaddress;
    private final int startServer;
    private final int endServer;

    private final LinkedList<Integer> localOccurrences = new LinkedList<>();
    private int checkedCount = 0;
    private int  occurrences = 0;
    HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    public BlacklistSearchThread(String ipaddress, int startServer, int endServer){
        this.ippaddress = ipaddress;
        this.startServer = startServer;
        this.endServer = endServer;
    }

    @Override
    public void run(){
        int i =startServer;
        while (i < endServer && occurrences < 5){
            
            checkedCount++;
            if(skds.isInBlackListServer(i, ippaddress)){
                localOccurrences.add(i);
                occurrences++;
            }
            i++;
        }
    }
    

    public List<Integer> getFoundBlacklists(){
        return localOccurrences;
    }

    public int getCheckedCount(){
        return checkedCount;
    }
    
    public int getOccurrencesCount(){
        return occurrences;
    }

}
