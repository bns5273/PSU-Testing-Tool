package lab11;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;
import java.util.ArrayList;


public class MyThread extends Thread {

    public MyThread() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    ArrayList<myClass> mca = new ArrayList<myClass>();
    VirtualMachine vm;
    boolean stopOnVMStart;
    boolean connected = true;
    int numClasses;
    String pkgName;

    public MyThread(VirtualMachine vm, boolean stopOnVMStart, String pkgName, int numClasses) {
        this.vm = vm;
        this.stopOnVMStart = stopOnVMStart;
        this.numClasses = numClasses;
        this.pkgName = pkgName;
        this.start();
    }

    public void run() {
        EventQueue queue = vm.eventQueue();
        for (int i = 1; i <= numClasses; i++) {
            ClassPrepareRequest cpr = vm.eventRequestManager().createClassPrepareRequest();
            cpr.addClassFilter(pkgName + ".*");
            cpr.addCountFilter(i);
            cpr.enable();
        }

        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                boolean resumeStoppedApp = false;
                EventIterator it = eventSet.eventIterator();
                while (it.hasNext()) {
                    Event e = it.nextEvent();
                    resumeStoppedApp |= !handleEvent(e);
                }
                if (resumeStoppedApp) {
                    eventSet.resume();
                }
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }

    private boolean handleEvent(Event event) {
        if (event instanceof ExceptionEvent) {
            return exceptionEvent(event);
        } else if (event instanceof BreakpointEvent) {
            return breakpointEvent(event);
        } else if (event instanceof WatchpointEvent) {
            return fieldWatchEvent(event);
        } else if (event instanceof StepEvent) {
            return stepEvent(event);
        } else if (event instanceof MethodEntryEvent) {
            return methodEntryEvent(event);
        } else if (event instanceof MethodExitEvent) {
            return methodExitEvent(event);
        } else if (event instanceof ClassPrepareEvent) {
            /*provide code
             The goal is to utilize the event object to identify methods in
             the reference type class. Find the locations of the methods 
             in the code. Use the virtual machine object to set breakpoints 
             for each location and enable them.
             */
//            System.out.println("ENTERED 1");
            
            ClassPrepareEvent cpe = (ClassPrepareEvent) event;
            Location l;
            for(Method m : cpe.referenceType().methods()){
//                System.out.println(m.name());
                l = m.location();
                BreakpointRequest e = vm.eventRequestManager().createBreakpointRequest(l);
                e.enable();
                
                for(myClass mc : mca){
                    int i = 0;
                    for(Method mm : mc.ma){
                        if(mm.equals(m)){
                            mc.la[i] = l;
                            System.out.println(l.lineNumber());
                        }
                        i++;
                    }
                }
            }
            return classPrepareEvent(event);
        } else if (event instanceof ClassUnloadEvent) {
            return classUnloadEvent(event);
        } else if (event instanceof ThreadStartEvent) {
            return threadStartEvent(event);
        } else if (event instanceof ThreadDeathEvent) {
            return threadDeathEvent(event);
        } else if (event instanceof VMStartEvent) {
            return vmStartEvent(event);
        } else {
            return handleExitEvent(event);
        }
    }

    private boolean vmDied = false;

    private boolean handleExitEvent(Event event) {
        if (event instanceof VMDeathEvent) {
            vmDied = true;
            return vmDeathEvent(event);
        } else if (event instanceof VMDisconnectEvent) {
            connected = false;
            if (!vmDied) {
                vmDisconnectEvent(event);
            }
            return false;
        } else {
            throw new InternalError("Unexpected event type");
        }
    }

    private boolean vmStartEvent(Event event) {
        VMStartEvent se = (VMStartEvent) event;
        return stopOnVMStart;
    }

    private boolean breakpointEvent(Event event) {
        /* provide code
           This method is called when a break point is encountered. 
           Take advantage of this break point event to identify the location.
           The method and class can be identified from the location. Make an 
           update to the number of times of execution for the class's method
           in real time.
         */

        BreakpointEvent be = (BreakpointEvent) event;
        Location loc = be.location();
        
        // find method / class from location
        for(myClass mc : mca){
            int i = 0;
            for(com.sun.jdi.Method mm : mc.ma){
                if(mc.la[i].equals(loc)){
                    System.out.println(loc.lineNumber());
                }
                i++;
            }
        }

        return false;
    }

    private boolean methodEntryEvent(Event event) {
        MethodEntryEvent me = (MethodEntryEvent) event;
        System.out.println("MethodEntryEvent");
        System.out.println(me.method().toString());
        System.out.println(me.location().lineNumber());
        return true;
    }

    private boolean methodExitEvent(Event event) {
        MethodExitEvent me = (MethodExitEvent) event;
        return true;
    }

    private boolean fieldWatchEvent(Event event) {
        WatchpointEvent fwe = (WatchpointEvent) event;
        return true;
    }

    private boolean stepEvent(Event event) {
        StepEvent se = (StepEvent) event;
        return true;
    }

    private boolean classPrepareEvent(Event event) {
        ClassPrepareEvent cle = (ClassPrepareEvent) event;
        return false;
    }

    private boolean classUnloadEvent(Event event) {
        ClassUnloadEvent cue = (ClassUnloadEvent) event;
        return false;
    }

    private boolean exceptionEvent(Event event) {
        ExceptionEvent ee = (ExceptionEvent) event;
        return true;
    }

    private boolean threadDeathEvent(Event event) {
        ThreadDeathEvent tee = (ThreadDeathEvent) event;
        return false;
    }

    private boolean threadStartEvent(Event event) {
        ThreadStartEvent tse = (ThreadStartEvent) event;
        return false;
    }

    public boolean vmDeathEvent(Event event) {
        return false;
    }

    public boolean vmDisconnectEvent(Event event) {
        System.out.println("VMDisconnectEvent");
        return false;
    }

    private void jbInit() throws Exception {
    }

}