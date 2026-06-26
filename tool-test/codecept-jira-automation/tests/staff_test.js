const { loginAsStaff } = require('./auth_helper');

Scenario('Login staff', async ({ I }) => {
    await loginAsStaff(I);

    I.wait(3);
    I.saveScreenshot('staff-login.png');
});