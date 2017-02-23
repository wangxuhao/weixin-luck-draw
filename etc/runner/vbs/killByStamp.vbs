cmdLine = ""
If WScript.Arguments.Count = 1 Then
    cmdLine = WScript.Arguments.Item(0)
Else
    Wscript.Echo "Usage: killByStamp.vbs <part of command line>"
    Wscript.Quit
End If

Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")

Set colProcess = objWMIService.ExecQuery("Select * from Win32_Process")

WScript.Echo ">>> Begin to find and terminate process with [" & cmdLine & "] ..."
For Each objProcess in colProcess
    cmd = objProcess.CommandLine & ""
    IF InStr(UCase(cmd), UCase(cmdLine))<>0 THEN
        If InStr (objProcess.CommandLine, WScript.ScriptName) <> 0 Then
            'Find current script it's self
        Else
    	    WScript.Echo ">>> Find process: " & cmd
            objProcess.Terminate()
	End If
    END IF    
Next
WScript.Echo ">>> Find and terminate process with [" & cmdLine & "] End."
