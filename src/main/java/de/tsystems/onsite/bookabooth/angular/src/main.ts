import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { appConfig } from './app/app.config';
import { App } from './app/app';

//appConfig erweitern
const config = {
  ...appConfig,
  providers: [...(appConfig.providers || []), provideHttpClient()],
};

bootstrapApplication(App, config).catch(err => console.error(err));
