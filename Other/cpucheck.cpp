#include <iostream>
#include <mach/mach.h>
#include <mach/mach_host.h>
#include <unistd.h>

double getCPUUsage() {
    host_cpu_load_info_data_t cpuInfo;
    mach_msg_type_number_t count = HOST_CPU_LOAD_INFO_COUNT;
    kern_return_t kr = host_statistics(mach_host_self(), HOST_CPU_LOAD_INFO, (host_info_t)&cpuInfo, &count);

    if (kr != KERN_SUCCESS) {
        std::cerr << "Error: " << mach_error_string(kr) << std::endl;
        return -1.0;
    }

    long totalTicks = 0;
    for (int i = 0; i < CPU_STATE_MAX; i++) {
        totalTicks += cpuInfo.cpu_ticks[i];
    }

    double cpuUsage = 100.0 * (double)(totalTicks - cpuInfo.cpu_ticks[CPU_STATE_IDLE]) / (double)totalTicks;
    return cpuUsage;
}

int main() {
    double usage = 0;

    while (true){
        if (usage >= 70 && usage < 90)
            std::cout << "Starts to be dangers" << std::endl;
        else if (usage >= 90)
            std::cout << "Damme, stop or cool" << std::endl;

        if (usage > 0) 
            std::cout << "Current CPU Usage: " << usage << "%" << std::endl;

        sleep(2);
        usage = getCPUUsage();   
    }

    return 0;
}
