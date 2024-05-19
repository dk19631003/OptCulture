import { AppVersion } from '@ionic-native/app-version/ngx';
import { SortPage } from './Store/sort/sort.page';
import { FilterPage } from './Store/filter/filter.page';
import { FormsModule } from '@angular/forms';
import { ViewRecepitComponent } from './components/view-recepit/view-recepit.component';
import { CustomTranslateLoaderService } from './services/custom-translate-loader.service';
import { LoaderInterceptor } from './services/LoaderInterceptor';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule, HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { DatePipe, LocationStrategy, HashLocationStrategy } from '@angular/common';
import { AndroidFingerprintAuth } from '@ionic-native/android-fingerprint-auth/ngx';
import { Push } from '@ionic-native/push/ngx';
import { ShowHideInputDirective } from './directives/show-hide-input.directive';
import { Keyboard } from '@ionic-native/keyboard/ngx';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { FilePath } from '@ionic-native/file-path/ngx';
import { Camera } from '@ionic-native/camera/ngx';
import { WebView } from '@ionic-native/ionic-webview/ngx';
import { FingerprintAIO } from '@ionic-native/fingerprint-aio/ngx';
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { Device } from '@ionic-native/device/ngx';
import { InAppBrowser } from '@ionic-native/in-app-browser/ngx';
import { FingerprintLoginConfirmationPopupComponent } from './components/fingerprint-login-confirmation-popup/fingerprint-login-confirmation-popup.component';


@NgModule({
  declarations: [AppComponent,
    ShowHideInputDirective,
    ViewRecepitComponent,
    FingerprintLoginConfirmationPopupComponent
    // FilterPage ,
    // SortPage
  ],
  entryComponents: [
    ViewRecepitComponent,
    // FilterPage,
    // SortPage
  ],
  imports: [
    FormsModule,
    BrowserModule,
    InfiniteScrollModule,
    IonicModule.forRoot({
      scrollAssist: true,
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: CustomTranslateLoaderService,
        deps: [HttpClient]
      }
    }),
    TranslateModule,
    AppRoutingModule,
    HttpClientModule,
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production, registrationStrategy: 'registerImmediately' }),
    ],
  providers: [
    StatusBar,
    SplashScreen,
    DatePipe,
    Keyboard,
    AndroidFingerprintAuth,
    Push,
    Camera,
    FilePath,
    WebView,
    AppVersion,
    FingerprintAIO,
    Device,
    InAppBrowser,
    { provide: HTTP_INTERCEPTORS, useClass: LoaderInterceptor, multi: true },
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    // {provide: LocationStrategy, useClass: HashLocationStrategy}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
