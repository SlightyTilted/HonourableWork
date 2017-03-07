using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RTMM_client_populator
{
//This program was run using visual studio. The resultant file was then ported to the Ubuntu Environment.


    class Program
    {
        static void Main(string[] args)
        {

            System.IO.StreamWriter file =  new System.IO.StreamWriter(@"C:\Users\Public\UserDB.csv");
            file.WriteLine("SEQUENTIAL");
            file.WriteLine("# This Line Will be Ignored");

            string a = "User";
            string b = "authentication username=";
            string c = " password=";
            for (int i = 0; i < 1000; i++)
            {
                string temp = a + Convert.ToString(i);
                file.WriteLine(temp + ";[" + b + temp + c + temp + "]");
            }
            file.Close();
        }
    }
}
