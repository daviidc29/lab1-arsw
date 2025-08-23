/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());


    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress) {
        int n = Math.max(1, Runtime.getRuntime().availableProcessors());
        return checkHost(ipaddress, n);
    }
    
    public List<Integer> checkHost(String ipaddress, int nThreads) {

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        int totalServers = skds.getRegisteredServersCount();

        if (nThreads <= 0) nThreads = 1;
        if (nThreads > totalServers) nThreads = totalServers;

        // División balanceada para N par o impar
        int base = totalServers / nThreads;
        int remainder = totalServers % nThreads;

        List<BlacklistSearchThread> workers = new ArrayList<>(nThreads);
        int start = 0;
        for (int i = 0; i < nThreads; i++) {
            int size = base + (i < remainder ? 1 : 0);
            int end = start + size;
            workers.add(new BlacklistSearchThread(ipaddress, start, end));
            start = end;
        }

        // Lanzar hilos
        for (Thread t : workers) t.start();

        // Esperar a que todos terminen
        for (Thread t : workers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Consolidar resultados
        LinkedList<Integer> blackListOccurrences = new LinkedList<>();
        int checkedListsCount = 0;

        for (BlacklistSearchThread w : workers) {
            checkedListsCount += w.getCheckedCount();
            blackListOccurrences.addAll(w.getFoundBlacklists());
        }

        // Política de decisión
        if (blackListOccurrences.size() >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        // *** Mantener el LOG (línea ~60 en tu versión) con datos verídicos ***
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}",
                new Object[]{checkedListsCount, totalServers});

        return blackListOccurrences;
    }    
    
    
}
