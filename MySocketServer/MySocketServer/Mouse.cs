using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Windows.Forms;

namespace MySocketServer
{
    class Mouse
    {
        [System.Runtime.InteropServices.DllImport("user32")]
        private static extern int mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);
        const int MOUSEEVENTF_MOVE = 0x0001;
        const int MOUSEEVENTF_LEFTDOWN = 0x0002;
        const int MOUSEEVENTF_LEFTUP = 0x0004;
        const int MOUSEEVENTF_RIGHTDOWN = 0x0008;
        const int MOUSEEVENTF_RIGHTUP = 0x0010;
        const int MOUSEEVENTF_MIDDLEDOWN = 0x0020;
        const int MOUSEEVENTF_MIDDLEUP = 0x0040;
        const int MOUSEEVENTF_ABSOLUTE = 0x8000;
        const int MOUSEEVENTF_WHEEL = 0x800;
        [DllImport("user32.dll")]
        public extern static void keybd_event(byte bvk, byte bScan, int dwFlags, int dwExtraInfo);

        public static void getMessage(String msg)
        {
            String[] instructionSet = msg.Split('|');
            String instruction = null;
            for (int t = 0; t < instructionSet.Length-1; t++)
            {
                switch (instructionSet[t].Substring(0, 1))
                {
                    case "0":
                        instruction = instructionSet[t].Substring(2, 1);
                        if (instruction == "0")
                        {
                            mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
                            mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
                            //     keybd_event(1, 0, 0, 0);
                            //     keybd_event(1, 0, 2, 0);

                        }
                        else if (instruction == "1")
                        {
                            mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
                            mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
                        }
                        else if (instruction == "2")
                        {
                            mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
                        }
                        else if (instruction == "3")
                        {
                            mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
                        }
                        break;
                    case "1":
                        instruction = instructionSet[t].Substring(2, instructionSet[t].Length-2);
                        String[] potisions = instruction.Split(',');
                        mouse_event(MOUSEEVENTF_MOVE, -Convert.ToInt32(potisions[1])/2, Convert.ToInt32(potisions[0])/2, 0, 0);
                        break;
                    case "2":
                        instruction = instructionSet[t].Substring(2, instructionSet[t].Length - 2);
                        mouse_event(MOUSEEVENTF_WHEEL,0, 0, Convert.ToInt32(instruction), 0);
                        break;
                    case "3":
                        instruction = instructionSet[t].Substring(2, instructionSet[t].Length - 2);
                        if (instruction == "0")
                        {
                            keybd_event((byte)Keys.LWin, 0, 0, 0); 
                            keybd_event((byte)Keys.Tab, 0, 0, 0); 
                            keybd_event((byte)Keys.LWin, 0, 2, 0);  
                            keybd_event((byte)Keys.Tab, 0, 2, 0);
                        }
                        else if (instruction == "1")
                        {

                            keybd_event((byte)Keys.LWin, 0, 0, 0);
                            keybd_event((byte)Keys.D, 0, 0, 0);
                            keybd_event((byte)Keys.LWin, 0, 2, 0);
                            keybd_event((byte)Keys.D, 0, 2, 0);

                        }
                        else if(instruction == "2")
                        {
                            keybd_event((byte)Keys.LWin, 0, 0, 0);
                            keybd_event((byte)Keys.ControlKey, 0, 0, 0); 
                            keybd_event((byte)Keys.Left, 0, 0, 0);
                           /// keybd_event((byte)Keys.D, 0, 2, 0);        
                            keybd_event((byte)Keys.LWin, 0, 2, 0); 
                            keybd_event((byte)Keys.ControlKey, 0, 2, 0); 
                            keybd_event((byte)Keys.Left, 0, 2, 0);


                            /*keybd_event(18, 0, 0, 0);
                            keybd_event((byte)Keys.Tab, 0, 0, 0);
                            keybd_event(18, 0, 2, 0);
                            keybd_event((byte)Keys.Tab, 0, 2, 0);*/

                            keybd_event(18, 0, 0, 0);
                            keybd_event((byte)Keys.Tab, 0, 0, 0);
                            keybd_event(18, 0, 2, 0);
                            keybd_event((byte)Keys.Tab, 0, 2, 0);
                        }
                        else if(instruction == "3")
                        {
                            keybd_event((byte)Keys.LWin, 0, 0, 0); 
                            keybd_event((byte)Keys.ControlKey, 0, 0, 0); 
                            keybd_event((byte)Keys.Right, 0, 0, 0);  
                            keybd_event((byte)Keys.LWin, 0, 2, 0); 
                            keybd_event((byte)Keys.ControlKey, 0, 2, 0); 
                            keybd_event((byte)Keys.Right, 0, 2, 0);

                            keybd_event(18, 0, 0, 0);
                            keybd_event((byte)Keys.Tab, 0, 0, 0);
                            keybd_event(18, 0, 2, 0);
                            keybd_event((byte)Keys.Tab, 0, 2, 0);


                        }
                        break;
                }
            }
        }
    }
}
