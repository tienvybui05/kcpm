require('dotenv').config();

async function loginAsDriver(I) {
    const res = await I.sendPostRequest(
        `${process.env.API_URL}/user-service/auth/login`,
        {
            soDienThoai: process.env.DRIVER_PHONE,
            matKhau: process.env.DRIVER_PASSWORD,
        }
    );

    const data = res.data;

    console.log('Driver login:', data);

    I.amOnPage('/');

    I.executeScript((data) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('userEmail', data.email);
        localStorage.setItem('userId', data.userId.toString());
        localStorage.setItem('userRole', data.role);
        localStorage.setItem('hoTen', data.hoTen);
    }, data);

    I.refreshPage();
}

async function loginAsStaff(I) {
    const res = await I.sendPostRequest(
        `${process.env.API_URL}/user-service/auth/login`,
        {
            soDienThoai: process.env.STAFF_PHONE,
            matKhau: process.env.STAFF_PASSWORD,
        }
    );

    const data = res.data;

    if (!data.token) {
        throw new Error(`Login failed: ${JSON.stringify(data)}`);
    }

    I.amOnPage('/');

    I.executeScript((data) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('userEmail', data.email);
        localStorage.setItem('userId', data.userId.toString());
        localStorage.setItem('userRole', data.role);
        localStorage.setItem('hoTen', data.hoTen);
    }, data);

    I.refreshPage();
}

async function loginAsAdmin(I) {
    const res = await I.sendPostRequest(
        `${process.env.API_URL}/user-service/auth/login`,
        {
            email: process.env.ADMIN_EMAIL,
            password: process.env.ADMIN_PASSWORD,
        }
    );

    const data = res.data;

    console.log('Admin login:', data);

    I.amOnPage('/');

    I.executeScript((data) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('userEmail', data.email);
        localStorage.setItem('userId', data.userId.toString());
        localStorage.setItem('userRole', data.role);
        localStorage.setItem('hoTen', data.hoTen);
    }, data);

    I.refreshPage();
}

module.exports = {
    loginAsDriver,
    loginAsStaff,
    loginAsAdmin,
};