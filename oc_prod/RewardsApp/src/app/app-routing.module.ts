import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: 'app', loadChildren: './tabs/tabs.module#TabsPageModule', canActivate: [AuthGuard]},
  { path: 'enrolment', loadChildren: './enrolment/enrolment.module#EnrolmentPageModule'  },
  { path: 'login', loadChildren: './login/login.module#LoginPageModule' },
  { path: 'otp', loadChildren: './login/otp/otp.module#OtpPageModule' },
  { path: 'forgot-password', loadChildren: './forgot-password/forgot-password.module#ForgotPasswordPageModule' },
  { path: '', loadChildren: './pages/pages.module#PagesModule', canActivate: [AuthGuard]},
  { path: 'program-details', loadChildren: './profile/program-details/program-details.module#ProgramDetailsPageModule' },
];
@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules, useHash: true })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
