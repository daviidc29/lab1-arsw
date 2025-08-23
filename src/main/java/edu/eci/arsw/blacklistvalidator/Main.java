/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 *
 * @author David
 */
public class Main {
    
    public static void main(String args[]){
        HostBlackListsValidator hblv=new HostBlackListsValidator();

        List<Integer> blackListOcurrences2=hblv.checkHost("212.24.24.55",100);
        System.out.println("The host was found in the following blacklists 2:"+blackListOcurrences2);

        
    }
        

        
    
    
}
