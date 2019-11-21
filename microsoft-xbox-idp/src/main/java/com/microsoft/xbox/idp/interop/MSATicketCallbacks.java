package com.microsoft.xbox.idp.interop;

import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.xbox.idp.jobs.MSAJob;
import com.microsoft.xbox.idp.jobs.MSAJob.Callbacks;

public class MSATicketCallbacks implements Callbacks {
    private String m_ticket = new String("");

    public String getTicket() {
        return this.m_ticket;
    }

    public void onUiNeeded(MSAJob job) {
        synchronized (job) {
            job.notifyAll();
        }
    }

    public void onFailure(MSAJob job, Exception e) {
        synchronized (job) {
            job.notifyAll();
        }
    }

    public void onUserCancel(MSAJob job) {
        synchronized (job) {
            job.notifyAll();
        }
    }

    public void onSignedOut(MSAJob job) {
        synchronized (job) {
            job.notifyAll();
        }
    }

    public void onAccountAcquired(MSAJob job, UserAccount userAccount) {
    }

    public void onTicketAcquired(MSAJob job, Ticket ticket) {
        synchronized (job) {
            this.m_ticket = ticket.getValue();
            job.notifyAll();
        }
    }
}
