interface LogBatConfig {
    appId: string;
}

interface Console {
    log: (...data: any[]) => void;
    error: (...data: any[]) => void;
}

class LogBat {
    private static appId: string = '';
    private static apiEndpoint: string = 'https://api.logbat.info/log';
    private static originalConsole: Console;

    public static init(config: LogBatConfig): void {
        this.appId = config.appId;
        this.overrideConsoleMethods();
    }

    public static log(...args: any[]): void {
        this.sendLog('info', args);
    }

    public static error(...args: any[]): void {
        this.sendLog('error', args);
    }

    private static sendLog(level: string, args: any[]): void {
        const logData = {
            level: level,
            created_at: new Date().toISOString(),
            data: args.map(arg =>
                typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
            ).join(' ')
        };

        fetch(this.apiEndpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'app_id': this.appId
            },
            body: JSON.stringify(logData),
        }).catch(err => this.originalConsole.error('Failed to send log:', err));
    }

    private static overrideConsoleMethods(): void {
        this.originalConsole = {
            log: console.log,
            error: console.error
        };

        console.log = (...args: any[]): void => {
            this.sendLog('info', args);
            this.originalConsole.log.apply(console, args);
        };

        console.error = (...args: any[]): void => {
            this.sendLog('error', args);
            this.originalConsole.error.apply(console, args);
        };
    }
}

export default LogBat;