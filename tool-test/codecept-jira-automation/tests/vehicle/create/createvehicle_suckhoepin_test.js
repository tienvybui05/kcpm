const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - Suc Khoe Pin");

Before(async ({ I }) => {
    await loginOnce(I);
});

Data([
    { value: "0" },
    { value: "1" },
    { value: "50" },
    { value: "99" },
    { value: "100" },
]).Scenario(
    "Standard BVA suc khoe pin",
    async ({ I, current }) => {
        await openAddVehicleModal(I);

        fillNominalVehicleForm(I);

        I.fillField("#sucKhoePin", current.value);

        I.click("Tạo Xe & Pin");

        // --- BỔ SUNG KÌ VỌNG VÀO ĐÂY ---
        // Ví dụ 1: Kiểm tra thông báo thành công xuất hiện
        I.see("Thêm xe mới và pin thành công!");

        // Ví dụ 2: Hoặc kiểm tra modal biến mất và dữ liệu mới xuất hiện ở bảng
        I.dontSee("Thêm Xe Mới & Tạo Pin");
    }
);  