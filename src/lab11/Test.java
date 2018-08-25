package lab11;



import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import javax.swing.JFileChooser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author wxw18
 */
public class Test {

    public static void main(String[] args) {
        Test test = new Test();
        JFileChooser jfc = new JFileChooser(".");
        LaunchingConnector lc = Bootstrap.virtualMachineManager().defaultConnector();
        Map map = lc.defaultArguments();
        Connector.Argument ca = (Connector.Argument) map.get("main");
        try {
//            File f = new File("/home/brett/Downloads/CoverageTool/urlclassloader/Application1.class");
            File f = null;
            int returnVal = jfc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                f = jfc.getSelectedFile();
            }
            String cName = "coveragetool" + "." + "MyProg";
            System.out.println("-cp \"" + f.getParentFile().getParentFile() + "\" " + cName);
            ca.setValue("-cp \"" + f.getParentFile().getParentFile() + "\" " + cName);
            VirtualMachine vm = lc.launch(map);
            Process process = vm.process();
            vm.setDebugTraceMode(VirtualMachine.TRACE_NONE);
            test.displayRemoteOutput(process.getInputStream());
            MyThread mt = new MyThread(vm, false, f.getParentFile().getName(), 1);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void displayRemoteOutput(final InputStream stream) {
        Thread thr = new Thread("output reader") {
            public void run() {
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                int i;
                try {
                    while ((i = in.read()) != -1) {
                        System.out.print((char) i); // Print out standard output
                    }
                } catch (IOException ex) {
                    System.out.println("Failed reading output");
                }
            }
        };
        thr.setPriority(Thread.MAX_PRIORITY - 1);
        thr.start();
    }
}
