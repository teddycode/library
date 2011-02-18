/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package navigators.smart.paxosatwar.messages;

import java.io.DataInput;
import java.io.IOException;
import java.io.ObjectInput;
import navigators.smart.communication.MessageHandler;
import navigators.smart.paxosatwar.requesthandler.RequestHandler;
import navigators.smart.paxosatwar.roles.Acceptor;
import navigators.smart.paxosatwar.roles.Proposer;
import navigators.smart.tom.core.messages.SystemMessage;
import navigators.smart.tom.core.messages.SystemMessage.Type;
import navigators.smart.tom.core.timer.messages.RTMessage;
import navigators.smart.tom.util.Logger;

import static navigators.smart.paxosatwar.messages.MessageFactory.COLLECT;


/**
 *
 * @author Christian Spann <christian.spann at uni-ulm.de>
 */
public class PaWMessageHandler implements MessageHandler{
    
    
    private Proposer proposer;
    private Acceptor acceptor;
    private RequestHandler reqhandler;

    public PaWMessageHandler(Acceptor acc, Proposer prop, RequestHandler reqhandlr){
        this.proposer = prop;
        this.acceptor = acc;
        this.reqhandler = reqhandlr;
    }
    
     public void setProposer(Proposer proposer) {
        this.proposer = proposer;
    }

    public void setAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    @Override
    public void processData(SystemMessage sm) {
        if (sm instanceof PaxosMessage) {
            PaxosMessage paxosMsg = (PaxosMessage) sm;
            //Logger.println("(TOMMessageHandler.processData) PAXOS_MSG received: " + paxosMsg);
            if (paxosMsg.getPaxosType() == COLLECT) {
                //the proposer layer only handle COLLECT messages
                proposer.deliver(paxosMsg);
            } else {
                acceptor.deliver(paxosMsg);
            }
        } else if (sm instanceof RTMessage) {
            RTMessage rtMsg = (RTMessage) sm;
            //Logger.println("(TOMMessageHandler.processData) RT_MSG received: "+rtMsg);
            reqhandler.deliverTimeoutRequest(rtMsg);
        }
    }

    public SystemMessage deserialise(Type type, DataInput in, Object result) throws ClassNotFoundException, IOException {
         switch(type){
            case PAXOS_MSG:
                return new PaxosMessage(in);
            default:
                Logger.println("Received msg for unknown msg type");
                return null;
        }
    }

}