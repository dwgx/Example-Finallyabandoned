package com.example.mod.protocol.heypixel.hwid;


import com.example.utils.math.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class ModuleInfos {
    public static ModuleInfo random() {
        return RandomUtils.nextList(moduleInfos);
    }

    public static class ModuleInfo {
        public int index;
        public String name;
        public String baseAddress;
        public String size;
        public String path;
        public String description;
        public String version;
        public String company;

        ModuleInfo(int index, String name, String baseAddress, String size, String path, String description, String version, String company) {
            this.index = index;
            this.name = name;
            this.baseAddress = baseAddress;
            this.size = size;
            this.path = path;
            this.description = description;
            this.version = version;
            this.company = company;
        }
    }

    private static final List<ModuleInfo> moduleInfos = new ArrayList<>();

    private static List<ModuleInfo> parseModules(String part1, String part2) {
        List<ModuleInfo> modules = new ArrayList<>();
        String combined = part1 + part2;
        String[] lines = combined.split("\n");

        for (String line : lines) {
            String[] parts = line.split("\t");
            if (parts.length >= 8) {
                int index = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim().toLowerCase();
                String baseAddress = parts[2].trim();
                String size = parts[3].trim();
                String path = parts[4].trim();
                String description = parts[5].trim();
                String version = parts[6].trim();
                String company = parts[7].trim();
                modules.add(new ModuleInfo(index, name, baseAddress, size, path, description, version, company));
            }
        }

        return modules;
    }

    public static void initializeHooks() {
        String part1 = """
            0	java.exe	0x7FF7CC400000	0xE000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\java.exe	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            1	ntdll.dll	0x7FFBF86B0000	0x217000	C:\\WINDOWS\\SYSTEM32\\ntdll.dll	NT 层 DLL	10.0.22621.4391	Microsoft Corporation
            2	KERNEL32.DLL	0x7FFBF8280000	0xC4000	C:\\WINDOWS\\System32\\KERNEL32.DLL	Windows NT 基本 API 客户端 DLL	10.0.22621.4391	Microsoft Corporation
            3	KERNELBASE.dll	0x7FFBF5B80000	0x3B9000	C:\\WINDOWS\\System32\\KERNELBASE.dll	Windows NT 基本 API 客户端 DLL	10.0.22621.4391	Microsoft Corporation
            4	ucrtbase.dll	0x7FFBF5F40000	0x111000	C:\\WINDOWS\\System32\\ucrtbase.dll	Microsoft® C Runtime Library	10.0.22621.3593	Microsoft Corporation
            5	jli.dll	0x7FFBD0B60000	0x18000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\jli.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            6	USER32.dll	0x7FFBF7660000	0x1AE000	C:\\WINDOWS\\System32\\USER32.dll	多用户 Windows 用户 API 客户端 DLL	10.0.22621.4391	Microsoft Corporation
            7	COMCTL32.dll	0x7FFBE6A00000	0x292000	C:\\WINDOWS\\WinSxS\\amd64_microsoft.windows.common-controls_6595b64144ccf1df_6.0.22621.4391_none_2715d37f73803e96\\COMCTL32.dll	用户体验控件库	6.10.22621.4391	Microsoft Corporation
            8	win32u.dll	0x7FFBF6060000	0x26000	C:\\WINDOWS\\System32\\win32u.dll	Win32u	10.0.22621.4460	Microsoft Corporation
            9	GDI32.dll	0x7FFBF83D0000	0x29000	C:\\WINDOWS\\System32\\GDI32.dll	GDI Client DLL	10.0.22621.4036	Microsoft Corporation
            10	msvcrt.dll	0x7FFBF7B30000	0xA7000	C:\\WINDOWS\\System32\\msvcrt.dll	Windows NT CRT DLL	7.0.22621.2506	Microsoft Corporation
            11	gdi32full.dll	0x7FFBF59B0000	0x11B000	C:\\WINDOWS\\System32\\gdi32full.dll	GDI Client DLL	10.0.22621.4391	Microsoft Corporation
            12	msvcp_win.dll	0x7FFBF6270000	0x9A000	C:\\WINDOWS\\System32\\msvcp_win.dll	Microsoft® C Runtime Library	10.0.22621.3374	Microsoft Corporation
            13	VCRUNTIME140.dll	0x7FFBCC360000	0x17000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\VCRUNTIME140.dll	Microsoft® C Runtime Library	14.0.24215.1	Microsoft Corporation
            14	IMM32.DLL	0x7FFBF7AF0000	0x31000	C:\\WINDOWS\\System32\\IMM32.DLL	Multi-User Windows IMM32 API Client DLL	10.0.22621.3374	Microsoft Corporation
            15	vcruntime140_1.dll	0x7FFBD07F0000	0xC000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\vcruntime140_1.dll	Microsoft® C Runtime Library	14.27.29016.0	Microsoft Corporation
            16	msvcp140.dll	0x7FFB76290000	0x9D000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\msvcp140.dll	Microsoft® C Runtime Library	14.0.24215.1	Microsoft Corporation
            17	jvm.dll	0x7FFB45060000	0xC47000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\server\\jvm.dll	OpenJDK 64-Bit server VM	17.0.2.0	Eclipse Adoptium
            18	ADVAPI32.dll	0x7FFBF8400000	0xB2000	C:\\WINDOWS\\System32\\ADVAPI32.dll	高级 Windows 32 基本 API	10.0.22621.4391	Microsoft Corporation
            19	sechost.dll	0x7FFBF7810000	0xA7000	C:\\WINDOWS\\System32\\sechost.dll	Host for SCM/SDDL/LSA Lookup APIs	10.0.22621.4391	Microsoft Corporation
            20	bcrypt.dll	0x7FFBF5AD0000	0x28000	C:\\WINDOWS\\System32\\bcrypt.dll	Windows 加密基元库	10.0.22621.2506	Microsoft Corporation
            21	RPCRT4.dll	0x7FFBF8550000	0x114000	C:\\WINDOWS\\System32\\RPCRT4.dll	远程过程调用运行时	10.0.22621.4249	Microsoft Corporation
            22	PSAPI.DLL	0x7FFBF6CD0000	0x8000	C:\\WINDOWS\\System32\\PSAPI.DLL	Process Status Helper	10.0.22621.1	Microsoft Corporation
            23	WSOCK32.dll	0x7FFBCFF60000	0x9000	C:\\WINDOWS\\SYSTEM32\\WSOCK32.dll	Windows Socket 32-Bit DLL	10.0.22621.1	Microsoft Corporation
            24	VERSION.dll	0x7FFBECF30000	0xA000	C:\\WINDOWS\\SYSTEM32\\VERSION.dll	Version Checking and File Installation Libraries	10.0.22621.1	Microsoft Corporation
            25	WS2_32.dll	0x7FFBF8350000	0x71000	C:\\WINDOWS\\System32\\WS2_32.dll	Windows Socket 2.0 32 位 DLL	10.0.22621.1	Microsoft Corporation
            26	SHLWAPI.dll	0x7FFBF7BE0000	0x5E000	C:\\WINDOWS\\System32\\SHLWAPI.dll	外壳简易实用工具库	10.0.22621.4391	Microsoft Corporation
            27	winmm.dll	0x7FFBEBBE0000	0x34000	C:\\WINDOWS\\system32\\winmm.dll	MCI API DLL	10.0.22621.4391	Microsoft Corporation
            28	kernel.appcore.dll	0x7FFBF4AE0000	0x18000	C:\\WINDOWS\\SYSTEM32\\kernel.appcore.dll	AppModel API Host	10.0.22621.3958	Microsoft Corporation
            29	jimage.dll	0x7FFBCF9A0000	0xA000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\jimage.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            30	DBGHELP.DLL	0x7FFBF3000000	0x232000	C:\\WINDOWS\\SYSTEM32\\DBGHELP.DLL	Windows Image Helper	10.0.22621.3593	Microsoft Corporation
            31	combase.dll	0x7FFBF63D0000	0x38F000	C:\\WINDOWS\\System32\\combase.dll	用于 Windows 的 Microsoft COM	10.0.22621.4391	Microsoft Corporation
            32	OLEAUT32.dll	0x7FFBF7580000	0xD7000	C:\\WINDOWS\\System32\\OLEAUT32.dll	OLEAUT32.DLL	10.0.22621.3672	Microsoft Corporation
            33	dbgcore.DLL	0x7FFBCABD0000	0x32000	C:\\WINDOWS\\SYSTEM32\\dbgcore.DLL	Windows Core Debugging Helpers	10.0.22621.1	Microsoft Corporation
            34	bcryptPrimitives.dll	0x7FFBF5B00000	0x7B000	C:\\WINDOWS\\System32\\bcryptPrimitives.dll	Windows Cryptographic Primitives Library	10.0.22621.4317	Microsoft Corporation
            35	java.dll	0x7FFBC3820000	0x25000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\java.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            36	jsvml.dll	0x7FFB5FCF0000	0xD6000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\jsvml.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            37	SHELL32.dll	0x7FFBF6D00000	0x876000	C:\\WINDOWS\\System32\\SHELL32.dll	Windows Shell 公用 DLL	10.0.22621.4460	Microsoft Corporation
            38	windows.storage.dll	0x7FFBF38F0000	0x903000	C:\\WINDOWS\\SYSTEM32\\windows.storage.dll	Microsoft WinRT Storage API	10.0.22621.4391	Microsoft Corporation
            39	wintypes.dll	0x7FFBF37B0000	0x13F000	C:\\WINDOWS\\SYSTEM32\\wintypes.dll	Windows 基本类型 DLL	10.0.22621.3810	Microsoft Corporation
            40	SHCORE.dll	0x7FFBF6B70000	0xF9000	C:\\WINDOWS\\System32\\SHCORE.dll	SHCORE	10.0.22621.4391	Microsoft Corporation
            41	profapi.dll	0x7FFBF58E0000	0x2B000	C:\\WINDOWS\\SYSTEM32\\profapi.dll	User Profile Basic API	10.0.22621.4391	Microsoft Corporation
            42	net.dll	0x7FFBCB640000	0x19000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\net.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            43	WINHTTP.dll	0x7FFBF03A0000	0x136000	C:\\WINDOWS\\SYSTEM32\\WINHTTP.dll	Windows HTTP 服务	10.0.22621.4391	Microsoft Corporation
            44	mswsock.dll	0x7FFBF4F60000	0x69000	C:\\WINDOWS\\system32\\mswsock.dll	Microsoft Windows Sockets 2.0 服务提供程序	10.0.22621.2506	Microsoft Corporation
            45	nio.dll	0x7FFBCB100000	0x15000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\nio.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            46	zip.dll	0x7FFBB7F30000	0x18000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\zip.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            47	verify.dll	0x7FFBCCA00000	0x10000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\verify.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            48	management.dll	0x7FFBCC350000	0x9000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\management.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            49	management_ext.dll	0x7FFBCBA50000	0xB000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\management_ext.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            50	napinsp.dll	0x7FFBB0390000	0x17000	C:\\WINDOWS\\system32\\napinsp.dll	电子邮件命名填充提供程序	10.0.22621.1	Microsoft Corporation
            51	pnrpnsp.dll	0x7FFBB0370000	0x1B000	C:\\WINDOWS\\system32\\pnrpnsp.dll	PNRP 命名空间提供程序	10.0.22621.1	Microsoft Corporation
            52	DNSAPI.dll	0x7FFBF4540000	0x102000	C:\\WINDOWS\\SYSTEM32\\DNSAPI.dll	DNS 客户端 API DLL	10.0.22621.4391	Microsoft Corporation
            53	IPHLPAPI.DLL	0x7FFBF44D0000	0x2D000	C:\\WINDOWS\\SYSTEM32\\IPHLPAPI.DLL	IP 帮助程序 API	10.0.22621.1	Microsoft Corporation
            54	NSI.dll	0x7FFBF6CE0000	0x9000	C:\\WINDOWS\\System32\\NSI.dll	NSI User-mode interface DLL	10.0.22621.1	Microsoft Corporation
            55	winrnr.dll	0x7FFBB03B0000	0x11000	C:\\WINDOWS\\System32\\winrnr.dll	LDAP RnR Provider DLL	10.0.22621.1	Microsoft Corporation
            56	wshbth.dll	0x7FFBD55B0000	0x15000	C:\\WINDOWS\\system32\\wshbth.dll	Windows Sockets Helper DLL	10.0.22621.3958	Microsoft Corporation
            57	nlansp_c.dll	0x7FFBB03D0000	0x27000	C:\\WINDOWS\\system32\\nlansp_c.dll	NLA Namespace Service Provider DLL	10.0.22621.4391	Microsoft Corporation
            58	rasadhlp.dll	0x7FFBEE770000	0xA000	C:\\Windows\\System32\\rasadhlp.dll	Remote Access AutoDial Helper	10.0.22621.1	Microsoft Corporation
            59	fwpuclnt.dll	0x7FFBEFD20000	0x83000	C:\\WINDOWS\\System32\\fwpuclnt.dll	FWP/IPsec 用户模式 API	10.0.22621.4249	Microsoft Corporation
            60	api-ms-win-crt-utility-l1-1-1.dll	0x7FFB41B30000	0x26D3000	D:\\MCLDownload\\Game\\.minecraft\\versions\\1.18\\natives\\runtime\\api-ms-win-crt-utility-l1-1-1.dll
            61	CRYPT32.dll	0x7FFBF6090000	0x166000	C:\\WINDOWS\\System32\\CRYPT32.dll	加密 API32	10.0.22621.4391	Microsoft Corporation
            62	libenvsdk.dll	0x7FFB40BA0000	0xF81000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\libenvsdk.dll	Netease Environment SDK	4.2.3.0	Netease
            63	WTSAPI32.dll	0x7FFBF4890000	0x14000	C:\\WINDOWS\\SYSTEM32\\WTSAPI32.dll	Windows Remote Desktop Session Host Server SDK APIs	10.0.22621.1	Microsoft Corporation
            64	ntmarta.dll	0x7FFBF4BF0000	0x34000	C:\\WINDOWS\\SYSTEM32\\ntmarta.dll	Windows NT MARTA 提供程序	10.0.22621.1	Microsoft Corporation
            65	SETUPAPI.DLL	0x7FFBF7C40000	0x474000	C:\\WINDOWS\\System32\\SETUPAPI.DLL	Windows 安装程序 API	10.0.22621.2506	Microsoft Corporation
            66	cfgmgr32.DLL	0x7FFBF5670000	0x4E000	C:\\WINDOWS\\SYSTEM32\\cfgmgr32.DLL	Configuration Manager DLL	10.0.22621.2506	Microsoft Corporation
            67	dhcpcsvc.DLL	0x7FFBF0360000	0x1F000	C:\\WINDOWS\\SYSTEM32\\dhcpcsvc.DLL	DHCP 客户端服务	10.0.22621.2506	Microsoft Corporation
            68	Ole32.dll	0x7FFBF6760000	0x1A5000	C:\\WINDOWS\\System32\\Ole32.dll	用于 Windows 的 Microsoft OLE	10.0.22621.3958	Microsoft Corporation
            69	CRYPTSP.dll	0x7FFBF51C0000	0x1B000	C:\\WINDOWS\\SYSTEM32\\CRYPTSP.dll	Cryptographic Service Provider API	10.0.22621.3672	Microsoft Corporation
            70	rsaenh.dll	0x7FFBF4A40000	0x35000	C:\\WINDOWS\\system32\\rsaenh.dll	Microsoft Enhanced Cryptographic Provider	10.0.22621.4249	Microsoft Corporation
            71	CRYPTBASE.dll	0x7FFBF51B0000	0xC000	C:\\WINDOWS\\SYSTEM32\\CRYPTBASE.dll	Base cryptographic API DLL	10.0.22621.1	Microsoft Corporation
            72	Wintrust.dll	0x7FFBF6200000	0x6C000	C:\\WINDOWS\\System32\\Wintrust.dll	Microsoft Trust Verification APIs	10.0.22621.4391	Microsoft Corporation
            73	MSASN1.dll	0x7FFBF5650000	0x12000	C:\\WINDOWS\\SYSTEM32\\MSASN1.dll	ASN.1 Runtime APIs	10.0.22621.2506	Microsoft Corporation
            74	Normaliz.dll	0x7FFBF6CF0000	0x8000	C:\\WINDOWS\\System32\\Normaliz.dll	Unicode Normalization DLL	10.0.22621.1	Microsoft Corporation
            75	clbcatq.dll	0x7FFBF69A0000	0xB0000	C:\\WINDOWS\\System32\\clbcatq.dll	COM+ Configuration Catalog	2001.12.10941.16384	Microsoft Corporation
            76	wbemprox.dll	0x7FFBED090000	0x10000	C:\\WINDOWS\\system32\\wbem\\wbemprox.dll	WMI	10.0.22621.3672	Microsoft Corporation
            77	wbemcomn.dll	0x7FFBED010000	0x80000	C:\\WINDOWS\\SYSTEM32\\wbemcomn.dll	WMI	10.0.22621.2506	Microsoft Corporation
            78	wbemsvc.dll	0x7FFBE9A50000	0x14000	C:\\WINDOWS\\system32\\wbem\\wbemsvc.dll	WMI	10.0.22621.3672	Microsoft Corporation
            79	fastprox.dll	0x7FFBE9A70000	0xF8000	C:\\WINDOWS\\system32\\wbem\\fastprox.dll	WMI Custom Marshaller	10.0.22621.4391	Microsoft Corporation
            80	amsi.dll	0x7FFBE54C0000	0x1D000	C:\\WINDOWS\\SYSTEM32\\amsi.dll	Anti-Malware Scan Interface	10.0.22621.3527	Microsoft Corporation
            81	USERENV.dll	0x7FFBF5050000	0x28000	C:\\WINDOWS\\SYSTEM32\\USERENV.dll	Userenv	10.0.22621.3527	Microsoft Corporation
            82	dhcpcsvc6.DLL	0x7FFBF0380000	0x19000	C:\\WINDOWS\\SYSTEM32\\dhcpcsvc6.DLL	DHCPv6 客户端	10.0.22621.2506	Microsoft Corporation
            83	jna9691996989552144293.dll	0x7FFB801A0000	0x44000	C:\\Users\\Administrator\\AppData\\Local\\Temp\\jna-146731693\\jna9691996989552144293.dll	JNA native library	6.1.1.0	Java(TM) Native Access (JNA)
            84	Pdh.dll	0x7FFBD0A70000	0x50000	C:\\WINDOWS\\SYSTEM32\\Pdh.dll	Windows 性能数据助手 DLL	10.0.22621.4391	Microsoft Corporation
            85	perfos.dll	0x7FFBD9F70000	0x10000	C:\\WINDOWS\\System32\\perfos.dll	Windows 系统性能对象 DLL	10.0.22621.1	Microsoft Corporation
            86	pfclient.dll	0x7FFBF2130000	0x10000	C:\\WINDOWS\\SYSTEM32\\pfclient.dll	SysMain Client	10.0.22621.1	Microsoft Corporation
            87	lwjgl.dll	0x7FFB7BA70000	0x72000	D:\\MCLDownload\\Game\\.minecraft\\versions\\1.18\\natives\\lwjgl.dll
            88	glfw.dll	0x7FFB79F50000	0x5E000	D:\\MCLDownload\\Game\\.minecraft\\versions\\1.18\\natives\\glfw.dll
            89	uxtheme.dll	0x7FFBF1F50000	0xB1000	C:\\WINDOWS\\system32\\uxtheme.dll	Microsoft UxTheme 库	10.0.22621.4391	Microsoft Corporation
            90	dinput8.dll	0x7FFB7AC50000	0x46000	C:\\WINDOWS\\SYSTEM32\\dinput8.dll	Microsoft DirectInput	10.0.22621.1	Microsoft Corporation
            91	xinput1_4.dll	0x7FFBB2720000	0x11000	C:\\WINDOWS\\SYSTEM32\\xinput1_4.dll	Microsoft 公共控制器 API	10.0.22621.1	Microsoft Corporation
            92	DEVOBJ.dll	0x7FFBF56C0000	0x2C000	C:\\WINDOWS\\SYSTEM32\\DEVOBJ.dll	Device Information Set DLL	10.0.22621.2506	Microsoft Corporation
            93	inputhost.dll	0x7FFBDCC90000	0x213000	C:\\WINDOWS\\SYSTEM32\\inputhost.dll	InputHost	10.0.22621.4391	Microsoft Corporation
            94	CoreMessaging.dll	0x7FFBF14A0000	0x133000	C:\\WINDOWS\\SYSTEM32\\CoreMessaging.dll	Microsoft CoreMessaging Dll	10.0.22621.4391	Microsoft Corporation
            95	dwmapi.dll	0x7FFBF2170000	0x2B000	C:\\WINDOWS\\SYSTEM32\\dwmapi.dll	Microsoft 桌面窗口管理器 API	10.0.22621.4391	Microsoft Corporation
            96	MSCTF.dll	0x7FFBF8120000	0x160000	C:\\WINDOWS\\System32\\MSCTF.dll	MSCTF 服务器 DLL	10.0.22621.4391	Microsoft Corporation
            97	HID.DLL	0x7FFBF4200000	0xE000	C:\\WINDOWS\\SYSTEM32\\HID.DLL	Hid 用户库	10.0.22621.1	Microsoft Corporation
            98	opengl32.dll	0x7FFB61F20000	0x100000	C:\\WINDOWS\\SYSTEM32\\opengl32.dll	OpenGL Client DLL	10.0.22621.4391	Microsoft Corporation
            99	GLU32.dll	0x7FFBD14B0000	0x2D000	C:\\WINDOWS\\SYSTEM32\\GLU32.dll	OpenGL 实用工具库 DLL	10.0.22621.2506	Microsoft Corporation
            100	dxcore.dll	0x7FFBF24F0000	0x37000	C:\\WINDOWS\\SYSTEM32\\dxcore.dll	DXCore	10.0.22621.4391	Microsoft Corporation
            101	AppXDeploymentClient.dll	0x7FFBEF1A0000	0x144000	C:\\Windows\\System32\\AppXDeploymentClient.dll	AppX 部署客户端 DLL	10.0.22621.4391	Microsoft Corporation
            102	nvoglv64.dll	0x7FFB4BEA0000	0x2638000	C:\\WINDOWS\\System32\\DriverStore\\FileRepository\\nv_dispi.inf_amd64_9425e4c3b1ac1c47\\nvoglv64.dll	NVIDIA Compatible OpenGL ICD	32.0.15.6636	NVIDIA Corporation
            103	cryptnet.dll	0x7FFBECEF0000	0x32000	C:\\WINDOWS\\SYSTEM32\\cryptnet.dll	Crypto Network Related API	10.0.22621.1	Microsoft Corporation
            104	wldp.dll	0x7FFBF5260000	0x49000	C:\\WINDOWS\\SYSTEM32\\wldp.dll	Windows 锁定策略	10.0.22621.4036	Microsoft Corporation
            105	drvstore.dll	0x7FFBECD80000	0x162000	C:\\WINDOWS\\SYSTEM32\\drvstore.dll	Driver Store API	10.0.22621.4391	Microsoft Corporation
            106	imagehlp.dll	0x7FFBF78C0000	0x1F000	C:\\WINDOWS\\System32\\imagehlp.dll	Windows NT Image Helper	10.0.22621.1	Microsoft Corporation
            107	nvgpucomp64.dll	0x7FFBE6CA0000	0x2D10000	C:\\WINDOWS\\System32\\DriverStore\\FileRepository\\nv_dispi.inf_amd64_9425e4c3b1ac1c47\\nvgpucomp64.dll	NVIDIA GPU Compiler Driver, Version 566.36	 32.0.15.6636	NVIDIA Corporation
            108	nvspcap64.dll	0x7FFBB23E0000	0x2FB000	C:\\WINDOWS\\system32\\nvspcap64.dll	NVIDIA Game Proxy	11.0.1.184	NVIDIA Corporation
            109	powrprof.dll	0x7FFBF4840000	0x4D000	C:\\WINDOWS\\SYSTEM32\\powrprof.dll	电源配置文件帮助程序 DLL	10.0.22621.3958	Microsoft Corporation
            110	UMPDC.dll	0x7FFBF4820000	0x13000	C:\\WINDOWS\\SYSTEM32\\UMPDC.dll	User Mode Power Dependency Coordinator	10.0.22621.1	Microsoft Corporation
            111	WINSTA.dll	0x7FFBF4450000	0x66000	C:\\WINDOWS\\SYSTEM32\\WINSTA.dll	Winstation Library	10.0.22621.4391	Microsoft Corporation
            112	textinputframework.dll	0x7FFBDCB40000	0x145000	C:\\WINDOWS\\SYSTEM32\\textinputframework.dll	"TextInputFramework.DYNLINK"	10.0.22621.4391	Microsoft Corporation
            113	lwjgl_opengl.dll	0x7FFB79DB0000	0x58000	D:\\MCLDownload\\Game\\.minecraft\\versions\\1.18\\natives\\lwjgl_opengl.dll
            114	lwjgl_stb.dll	0x7FFB76100000	0x7F000	D:\\MCLDownload\\Game\\.minecraft\\versions\\1.18\\natives\\lwjgl_stb.dll
            115	mscms.dll	0x7FFBEDEE0000	0xBC000	C:\\WINDOWS\\SYSTEM32\\mscms.dll	Microsoft 颜色匹配系统 DLL	10.0.22621.4455	Microsoft Corporation
            116	icm32.dll	0x7FFB813E0000	0x49000	C:\\WINDOWS\\SYSTEM32\\icm32.dll	Microsoft Color Management Module (CMM)	10.0.22621.4455	Microsoft Corporation
            117	sunmscapi.dll	0x7FFBCB880000	0xE000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\sunmscapi.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            118	ncrypt.dll	0x7FFBF5350000	0x2D000	C:\\WINDOWS\\SYSTEM32\\ncrypt.dll	Windows NCrypt 路由器	10.0.22621.4317	Microsoft Corporation"""; // 替换为实际的 part1 数据
        String part2 = """
            119	NTASN1.dll	0x7FFBF5310000	0x37000	C:\\WINDOWS\\SYSTEM32\\NTASN1.dll	Microsoft ASN.1 API	10.0.22621.1	Microsoft Corporation
            120	perfdisk.dll	0x7FFBCB750000	0x10000	C:\\WINDOWS\\System32\\perfdisk.dll	Windows 磁盘性能对象 DLL	10.0.22621.1	Microsoft Corporation
            121	WMICLNT.dll	0x7FFBEFF10000	0x11000	C:\\WINDOWS\\System32\\WMICLNT.dll	WMI Client API	10.0.22621.1	Microsoft Corporation
            122	gpapi.dll	0x7FFBF4FF0000	0x26000	C:\\WINDOWS\\SYSTEM32\\gpapi.dll	组策略客户端 API	10.0.22621.3810	Microsoft Corporation
            123	WINNSI.DLL	0x7FFBF20F0000	0xD000	C:\\WINDOWS\\SYSTEM32\\WINNSI.DLL	Network Store Information RPC interface	10.0.22621.1	Microsoft Corporation
            124	OpenAL.dll	0x7FFB4F980000	0x114000	D:\\MCLDownload\\Game\\.minecraft\\versions\\1.18\\natives\\OpenAL.dll
            125	MMDevApi.dll	0x7FFBE4AB0000	0x9E000	C:\\WINDOWS\\System32\\MMDevApi.dll	MMDevice API	10.0.22621.4111	Microsoft Corporation
            126	SAPIWrapper_x64.dll	0x7FFBAC040000	0x1A000	D:\\MCLDownload\\Game\\.minecraft\\versions\\1.18\\natives\\SAPIWrapper_x64.dll
            127	sapi.dll	0x7FFB4EE70000	0x182000	C:\\WINDOWS\\System32\\Speech\\Common\\sapi.dll	语音 API	5.3.29816.0	Microsoft Corporation
            128	AUDIOSES.DLL	0x7FFBD7600000	0x1ED000	C:\\WINDOWS\\SYSTEM32\\AUDIOSES.DLL	音频会话	10.0.22621.4391	Microsoft Corporation
            129	resourcepolicyclient.dll	0x7FFBF24D0000	0x15000	C:\\WINDOWS\\SYSTEM32\\resourcepolicyclient.dll	Resource Policy Client	10.0.22621.3527	Microsoft Corporation
            130	wshunix.dll	0x7FFBDA400000	0x8000	C:\\WINDOWS\\system32\\wshunix.dll	AF_UNIX Winsock2 Helper DLL	10.0.22621.1	Microsoft Corporation
            131	CoreUIComponents.dll	0x7FFBEF360000	0x36D000	C:\\WINDOWS\\SYSTEM32\\CoreUIComponents.dll	Microsoft Core UI Components Dll	10.0.22621.4391	Microsoft Corporation
            132	awt.dll	0x7FFB40A10000	0x18E000	D:\\MCLDownload\\ext\\jre-v64-220420\\jdk17\\bin\\awt.dll	OpenJDK Platform binary	17.0.2.0	Eclipse Adoptium
            133	apphelp.dll	0x7FFBF1940000	0x97000	C:\\WINDOWS\\SYSTEM32\\apphelp.dll	应用程序兼容性客户端库	10.0.22621.4391	Microsoft Corporation
            134	javaw.exe	0x7FFBF1940000	0x97000	C:\\WINDOWS\\SYSTEM32\\apphelp.dll	应用程序兼容性客户端库	10.0.22621.4391	Microsoft Corporation"""; // 替换为实际的 part2 数据
        moduleInfos.addAll(parseModules(part1, part2));
    }

    static {
        initializeHooks();
    }
}
