# Culture = "en-US"
ConvertFrom-StringData @'
    PromptText = [Y] Yes [N] No (default is "N")
    PromptYesCharacter = Y
    PromptNoCharacter = N
    PromptYesString = Yes
    PromptNoString = No
    PackageFound = Found developer package: {0}
    CertificateFound = Found developer certificate: {0}
    DependenciesFound = Found dependency package(s):
    GettingDeveloperLicense = Acquiring developer license...
    InstallingCertificate = Installing developer certificate...
    InstallingPackage = \nInstalling developer package...
    AcquireLicenseSuccessful = A developer license was successfully acquired.
    InstallCertificateSuccessful = The developer certificate was successfully installed.
    Success = \nSuccess: Your developer package was successfully installed.
    WarningInstallCert = You are about to install a digital certificate to your machine's Trusted People certificate store.  Doing so carries serious security risk and should only be done if you trust the originator of this digital certificate.\n\nWhen you are done using this app, you should manually remove the associated digital certificate.  Instructions for doing so can be found here:\nhttp://go.microsoft.com/fwlink/?LinkId=243053
    WarningPromptContinue = \nAre you sure you wish to continue?
    ElevateActions = \nBefore installing this developer package, you need to do the following:
    ElevateActionDevLicense = \t- Acquire a developer license
    ElevateActionCertificate = \t- Install the developer certificate
    ElevateActionsContinue = Administrator credentials are required to continue.  Please accept the UAC prompt and provide your administrator password if asked.
    ErrorForceElevate = You must provide administrator credentials to proceed.  Please run this script without the -Force parameter or from an elevated PowerShell window.
    ErrorForceDeveloperLicense = Acquiring a developer license requires user interaction.  Please rerun the script without the -Force parameter.
    ErrorLaunchAdminFailed = Error: Could not start a new process as administrator.
    ErrorNoScriptPath = Error: You must launch this script from a file.
    ErrorNoPackageFound = Error: No developer package found.
    ErrorManyPackagesFound = Error: More than one developer package found.
    ErrorPackageUnsigned = Error: The developer package is not digitally signed or its signature is corrupted.
    ErrorNoCertificateFound = Error: No developer certificate found.
    ErrorManyCertificatesFound = Error: More than one developer certificate found.
    ErrorBadCertificate = Error: The file "{0}" is not a valid digital certificate.  CertUtil returned with error code {1}.
    ErrorCertificateMismatch = Error: The developer certificate does not match the certificate used to sign the developer package.
    ErrorCertIsCA = Error: The developer certificate can't be a certificate authority.
    ErrorBannedKeyUsage = Error: The developer certificate can't have the following key usage: {0}.  Key usage must be unspecified or equal to "DigitalSignature".
    ErrorBannedEKU = Error: The developer certificate can't have the following extended key usage: {0}.  Only the Code Signing and Lifetime Signing EKUs are allowed.
    ErrorNoBasicConstraints = Error: The developer certificate is missing the basic constraints extension.
    ErrorNoCodeSigningEku = Error: The developer certificate is missing the extended key usage for Code Signing.
    ErrorInstallCertificateCancelled = Error: Installation of the certificate was cancelled.
    ErrorCertUtilInstallFailed = Error: Could not install the developer certificate.  CertUtil returned with error code {0}.
    ErrorGetDeveloperLicenseFailed = Error: Could not acquire a developer license.
    ErrorInstallCertificateFailed = Error: Could not install the developer certificate.
    ErrorAddPackageFailed = Error: Could not install the developer package.
    ErrorAddPackageFailedWithCert = Error: Could not install the developer package.  To ensure security, please consider removing the developer certificate until you can install the package.  Instructions for doing so can be found here:\nhttp://go.microsoft.com/fwlink/?LinkId=243053
'@
