package org.rtmm.prac1;

public enum RTSPState 
{
     INIT ("INIT"),
     READY ("READY"),
     PLAYING ("PLAYING");
     
     private String desc;
     
     RTSPState(String desc){
         this.desc = desc;
     }
     
     public String toString(){
         return desc;
     }
}

