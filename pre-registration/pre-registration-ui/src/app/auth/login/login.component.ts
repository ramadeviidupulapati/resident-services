import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { FormControl, Validators } from '@angular/forms';
import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';
import { MatDialog } from '@angular/material';
import { AuthService } from '../auth.service';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  languages: string[] = ['French', 'Arabic'];

  inputPlaceholderContact = 'Email ID or Phone Number';
  inputPlaceholderOTP = 'Enter OTP';
  disableBtn = false;
  timer: any;
  secondaryLangCode = 'ar';
  secondaryDir = 'rtl';
  inputContactDetails = '';
  inputOTP: string;
  selectedLanguage = '';
  langCode = 'ara';
  dir = 'ltr';
  showSendOTP = true;
  showResend = false;
  showVerify = false;
  showContactDetails = true;
  showOTP = false;

  email = new FormControl('', [Validators.required, Validators.email]);

  getErrorMessage() {
    return this.email.hasError('required')
      ? 'You must enter a value'
      : this.email.hasError('email')
      ? 'Not a valid email'
      : '';
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private translate: TranslateService,
    private dialog: MatDialog
  ) {
    const loggedOut = localStorage.getItem('loggedOut');
    localStorage.clear();
    translate.addLangs(['eng', 'fra', 'ara']);
    translate.setDefaultLang('ara');

    // const browserLang = translate.getBrowserLang();
    // translate.use(browserLang.match(/eng|fra|ara/) ? browserLang : 'ara');
    localStorage.setItem('loggedOut', loggedOut);
    localStorage.setItem('langCode', this.langCode);
  }

  ngOnInit() {
    if (localStorage.getItem('langCode')) {
      this.langCode = localStorage.getItem('langCode');
      this.translate.use(this.langCode);
    }
    if (localStorage.getItem('loggedOut') && localStorage.getItem('loggedOut') === 'true') {
      localStorage.removeItem('loggedOut');
      const data = {
        case: 'MESSAGE',
        message: 'Applicant has to login with same Mobile/Email to access the created applications'
      };
      this.dialog.open(DialougComponent, {
        width: '350px',
        data: data
      });
    }
    localStorage.setItem('loggedIn', 'false');
  }

  changeLanguage(): void {
    if (this.selectedLanguage === 'English') {
      this.langCode = 'eng';
      this.secondaryLangCode = 'ara';
      this.dir = 'ltr';
      this.secondaryDir = 'rtl';
    } else if (this.selectedLanguage === 'French') {
      this.langCode = 'fra';
      this.dir = 'ltr';
      this.secondaryLangCode = 'ara';
      this.secondaryDir = 'rtl';
    } else if (this.selectedLanguage === 'Arabic') {
      this.langCode = 'ara';
      this.dir = 'rtl';
      this.secondaryLangCode = 'fra';
      this.secondaryDir = 'ltr';
    }
    this.translate.use(this.langCode);
    localStorage.setItem('langCode', this.langCode);
    localStorage.setItem('secondaryLangCode', this.secondaryLangCode);
    localStorage.setItem('dir', this.dir);
    localStorage.setItem('secondaryDir', this.secondaryDir);
  }

  showVerifyBtn() {
    if (this.inputOTP.length > 3) {
      this.showVerify = true;
      this.showResend = false;
    } else {
      this.showResend = true;
      this.showVerify = false;
    }
  }

  submit(): void {
    if (this.showSendOTP || this.showResend) {
      this.showResend = true;
      this.showOTP = true;
      this.showSendOTP = false;
      this.showContactDetails = false;

      const timerFn = () => {
        let secValue = Number(document.getElementById('secondsSpan').innerText);
        const minValue = Number(document.getElementById('minutesSpan').innerText);

        if (secValue === 0) {
          secValue = 60;
          if (minValue === 0) {
            // redirecting to initial phase on completion of timer
            this.showContactDetails = true;
            this.showSendOTP = true;
            this.showResend = false;
            this.showOTP = false;
            this.showVerify = false;
            document.getElementById('minutesSpan').innerText = '02';

            document.getElementById('timer').style.visibility = 'hidden';
            clearInterval(this.timer);
            return;
          }

          document.getElementById('minutesSpan').innerText = '0' + (minValue - 1);
        }

        if (secValue === 10 || secValue < 10) {
          document.getElementById('secondsSpan').innerText = '0' + --secValue;
        } else {
          document.getElementById('secondsSpan').innerText = --secValue + '';
        }
      };

      // update of timer value on click of resend
      if (document.getElementById('timer').style.visibility === 'visible') {
        document.getElementById('secondsSpan').innerText = '00';
        document.getElementById('minutesSpan').innerText = '02';
      } else {
        // initial set up for timer
        document.getElementById('timer').style.visibility = 'visible';
        this.timer = setInterval(timerFn, 1000);
      }

      // dynamic update of button text for Resend and Verify
    } else if (this.showVerify) {
      clearInterval(this.timer);
      localStorage.setItem('loggedIn', 'true');
      // this.authService.setToken();
      this.router.navigate(['dashboard', this.inputContactDetails]);
    }
  }
}
