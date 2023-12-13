import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'incasable.integration',
  appName: 'incasable-integration',
  webDir: 'dist',
  server: {
    androidScheme: 'https'
  }
};

export default config;
