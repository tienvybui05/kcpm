// components/CarManagement/Modals/AddCarModal.js
import { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import Button from "../../../../components/Shares/Button/Button";
import styles from "../CarManagement.module.css";
import {
  faCarSide,
  faBatteryFull,
  faPlus,
} from "@fortawesome/free-solid-svg-icons";

function AddCarModal({ isOpen, onClose, onSuccess, maTaiXe }) {
  const [loadingModels, setLoadingModels] = useState(false);
  const [uniqueModels, setUniqueModels] = useState([]);
  const [creatingPin, setCreatingPin] = useState(false);

  // State quản lý lỗi hiển thị trên UI
  const [errors, setErrors] = useState({});

  const [newCar, setNewCar] = useState({
    vin: "",
    bienSo: "",
    loaiXe: "",
    selectedPinType: "",
    loaiPin: "",
    dungLuongPin: "",
    sucKhoePin: "100",
  });

  const loadUniqueModels = async () => {
    const token = localStorage.getItem("token");
    setLoadingModels(true);
    try {
      const pinsRes = await fetch("/api/battery-service/pins", {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });

      if (pinsRes.ok) {
        const pinsData = await pinsRes.json();

        const uniqModels = [];
        const seen = new Set();
        pinsData.forEach((p) => {
          const model = (p.loaiPin ?? p.loai_pin ?? "").trim();
          if (model && !seen.has(model)) {
            seen.add(model);
            uniqModels.push({
              loaiPin: model,
              dungLuong: p.dungLuong ?? p.dung_luong ?? "",
            });
          }
        });
        setUniqueModels(uniqModels);
      } else {
        console.error("Lỗi tải danh sách model pin");
        setUniqueModels([]);
      }
    } catch (err) {
      console.error("Lỗi kết nối khi tải model pin:", err);
      setUniqueModels([]);
    } finally {
      setLoadingModels(false);
    }
  };

  const handlePinTypeChange = (selectedType) => {
    if (selectedType) {
      const found = uniqueModels.find((m) => m.loaiPin === selectedType);
      if (found) {
        setNewCar((prev) => ({
          ...prev,
          selectedPinType: selectedType,
          loaiPin: found.loaiPin,
          dungLuongPin: found.dungLuong.toString(),
        }));
      } else {
        setNewCar((prev) => ({
          ...prev,
          selectedPinType: selectedType,
          loaiPin: selectedType,
          dungLuongPin: "",
        }));
      }
    } else {
      setNewCar((prev) => ({
        ...prev,
        selectedPinType: selectedType,
        loaiPin: "",
        dungLuongPin: "",
      }));
    }
  };

  const createNewPinForCar = async (token, carInfo) => {
    const pinPayload = {
      loaiPin: carInfo.loaiPin,
      dungLuong: carInfo.dungLuongPin ? parseFloat(carInfo.dungLuongPin) : 0,
      tinhTrang: "DAY",
      trangThaiSoHuu: "DANG_SU_DUNG",
      sucKhoe: carInfo.sucKhoePin ? parseFloat(carInfo.sucKhoePin) : 100,
      ngayNhapKho: new Date().toISOString().split("T")[0],
      ngayBaoDuongGanNhat: new Date().toISOString().split("T")[0],
    };

    const response = await fetch("/api/battery-service/pins", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(pinPayload),
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(`Tạo pin thất bại: ${error}`);
    }

    return await response.json();
  };

  // --- Regex định dạng biển số xe VN (VD: 51A-1234, 51A-12345, 51A-123.45) ---
  const regexBienSo =
    /^[0-9]{2}[A-Za-z]{1,2}-[0-9]{4,5}$|^[0-9]{2}[A-Za-z]{1,2}-[0-9]{3}\.[0-9]{2}$/;

  const handleSaveNew = async () => {
    const token = localStorage.getItem("token");

    if (!maTaiXe) {
      alert("Không tìm thấy thông tin tài xế!");
      return;
    }

    // Kiểm tra toàn bộ lỗi trước khi submit
    let hasError = false;
    let newErrors = {};

    // Validate Loại xe
    if (!newCar.loaiXe.trim()) {
      newErrors.loaiXe = "Không được loại xe để trống";
      hasError = true;
    }

    // Validate Biển số
    const trimmedBienSo = newCar.bienSo.trim();
    if (!trimmedBienSo) {
      newErrors.bienSo = "Không được để biển số trống";
      hasError = true;
    } else if (!regexBienSo.test(trimmedBienSo)) {
      newErrors.bienSo = "Không hợp lệ";
      hasError = true;
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
    }

    if (!newCar.vin.trim() || !newCar.bienSo.trim() || !newCar.loaiXe.trim()) {
      return; // Chặn không cho gọi API nếu form trống
    }

    if (hasError) return; // Chặn nếu dính regex hoặc trống

    if (!newCar.selectedPinType) {
      alert("Vui lòng chọn loại pin!");
      return;
    }

    if (
      !newCar.sucKhoePin ||
      newCar.sucKhoePin < 0 ||
      newCar.sucKhoePin > 100
    ) {
      alert("Vui lòng nhập sức khỏe pin từ 0-100%!");
      return;
    }

    setCreatingPin(true);

    try {
      const newPin = await createNewPinForCar(token, newCar);

      const carPayload = {
        vin: newCar.vin.trim(),
        bienSo: newCar.bienSo.trim(),
        loaiXe: newCar.loaiXe.trim(),
        maTaiXe: maTaiXe,
        maPin: newPin.maPin,
      };

      const carResponse = await fetch("/api/vehicle-service/vehicles", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(carPayload),
      });

      if (carResponse.ok) {
        alert(`Thêm xe thành công! Đã tạo pin #${newPin.maPin} cho xe.`);
        onSuccess();
      } else {
        const error = await carResponse.text();
        await fetch(`/api/battery-service/pins/${newPin.maPin}`, {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        });
        alert("Thêm xe thất bại!\n" + error);
      }
    } catch (error) {
      console.error("Lỗi thêm xe và pin:", error);
      alert("Lỗi: " + error.message);
    } finally {
      setCreatingPin(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      setNewCar({
        vin: "",
        bienSo: "",
        loaiXe: "",
        selectedPinType: "",
        loaiPin: "",
        dungLuongPin: "",
        sucKhoePin: "100",
      });
      setErrors({});
      loadUniqueModels();
    }
  }, [isOpen]);

  // Xử lý khi blur Loại xe
  const handleBlurLoaiXe = () => {
    if (!newCar.loaiXe.trim()) {
      setErrors((prev) => ({ ...prev, loaiXe: "Không được loại xe để trống" }));
    } else {
      setErrors((prev) => ({ ...prev, loaiXe: "" }));
    }
  };

  // Xử lý khi blur Biển số
  const handleBlurBienSo = () => {
    const trimmedBienSo = newCar.bienSo.trim();
    if (!trimmedBienSo) {
      setErrors((prev) => ({ ...prev, bienSo: "Không được để biển số trống" }));
    } else if (!regexBienSo.test(trimmedBienSo)) {
      setErrors((prev) => ({ ...prev, bienSo: "Không hợp lệ" }));
    } else {
      setErrors((prev) => ({ ...prev, bienSo: "" }));
    }
  };

  if (!isOpen) return null;

  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modal}>
        <h2>
          <FontAwesomeIcon
            icon={faCarSide}
            style={{ marginRight: "10px", color: "#007bff" }}
          />
          Thêm Xe Mới & Tạo Pin
        </h2>

        <div className={styles.sectionDivider}>
          <h4>Thông tin Xe</h4>
        </div>
        <div className={styles.formdetail}>
          <label htmlFor="vin">Mã VIN *</label>
          <input
            id="vin"
            type="text"
            value={newCar.vin}
            onChange={(e) => setNewCar({ ...newCar, vin: e.target.value })}
            placeholder="VD: VNFAST998877"
          />
        </div>

        {/* ================= KHU VỰC BIỂN SỐ ================= */}
        <div className={styles.formdetail}>
          <label htmlFor="bienSo">Biển số *</label>
          <input
            id="bienSo"
            type="text"
            value={newCar.bienSo}
            onChange={(e) => {
              setNewCar({ ...newCar, bienSo: e.target.value });
              // Ẩn lỗi khi người dùng bắt đầu gõ lại
              if (errors.bienSo) setErrors((prev) => ({ ...prev, bienSo: "" }));
            }}
            onBlur={handleBlurBienSo} // Gọi validation khi Tab ra ngoài
            placeholder="VD: 51A-123.45"
          />
          {errors.bienSo && (
            <span
              style={{
                color: "red",
                fontSize: "12px",
                marginTop: "4px",
                display: "block",
              }}
            >
              {errors.bienSo}
            </span>
          )}
        </div>
        {/* ================================================= */}

        {/* ================= KHU VỰC LOẠI XE ================= */}
        <div className={styles.formdetail}>
          <label htmlFor="loaiXe">Loại xe *</label>
          <input
            id="loaiXe"
            type="text"
            value={newCar.loaiXe}
            onChange={(e) => {
              setNewCar({ ...newCar, loaiXe: e.target.value });
              if (errors.loaiXe) setErrors((prev) => ({ ...prev, loaiXe: "" }));
            }}
            onBlur={handleBlurLoaiXe}
            placeholder="VD: VinFast VF8"
          />
          {errors.loaiXe && (
            <span
              style={{
                color: "red",
                fontSize: "12px",
                marginTop: "4px",
                display: "block",
              }}
            >
              {errors.loaiXe}
            </span>
          )}
        </div>
        {/* ================================================= */}

        <div className={styles.sectionDivider}>
          <h4>
            <FontAwesomeIcon
              icon={faBatteryFull}
              style={{ marginRight: "8px", color: "#28a745" }}
            />
            Thông tin Pin
          </h4>
        </div>

        <div className={styles.formdetail}>
          <label htmlFor="pinType">Loại pin *</label>
          <input
            list="modelList"
            id="pinType"
            value={newCar.selectedPinType}
            onChange={(e) => handlePinTypeChange(e.target.value)}
            placeholder="Nhập hoặc chọn model có sẵn"
          />
          <datalist id="modelList">
            {uniqueModels.map((model) => (
              <option key={model.loaiPin} value={model.loaiPin} />
            ))}
          </datalist>
          {loadingModels && <small>Đang tải danh sách model...</small>}
        </div>

        <div className={styles.formdetail}>
          <label htmlFor="loaiPin">Thông số pin</label>
          <input
            id="loaiPin"
            type="text"
            value={newCar.loaiPin}
            onChange={(e) => setNewCar({ ...newCar, loaiPin: e.target.value })}
            placeholder="Loại pin sẽ tự động điền khi chọn model"
            className={
              newCar.selectedPinType &&
              uniqueModels.find((m) => m.loaiPin === newCar.selectedPinType)
                ? styles.readonlyInput
                : ""
            }
            readOnly={
              !!(
                newCar.selectedPinType &&
                uniqueModels.find((m) => m.loaiPin === newCar.selectedPinType)
              )
            }
          />
        </div>

        <div className={styles.formdetail}>
          <label htmlFor="dungLuongPin">Dung lượng pin (kWh)</label>
          <input
            id="dungLuongPin"
            type="number"
            value={newCar.dungLuongPin}
            onChange={(e) =>
              setNewCar({ ...newCar, dungLuongPin: e.target.value })
            }
            placeholder="Dung lượng sẽ tự động điền khi chọn model"
            className={
              newCar.selectedPinType &&
              uniqueModels.find((m) => m.loaiPin === newCar.selectedPinType)
                ? styles.readonlyInput
                : ""
            }
            readOnly={
              !!(
                newCar.selectedPinType &&
                uniqueModels.find((m) => m.loaiPin === newCar.selectedPinType)
              )
            }
          />
        </div>

        <div className={styles.formdetail}>
          <label htmlFor="sucKhoePin">Sức khỏe pin (%) *</label>
          <input
            id="sucKhoePin"
            type="number"
            min="0"
            max="100"
            step="1"
            value={newCar.sucKhoePin}
            onChange={(e) =>
              setNewCar({ ...newCar, sucKhoePin: e.target.value })
            }
            placeholder="Nhập từ 0-100%"
          />
          <div className={styles.rangeInfo}>
            <span>0%</span>
            <span>50%</span>
            <span>100%</span>
          </div>
        </div>

        <div className={styles.modalActions}>
          <Button
            change
            type="button"
            onClick={handleSaveNew}
            disabled={creatingPin || !newCar.selectedPinType || !maTaiXe}
          >
            <FontAwesomeIcon icon={faPlus} style={{ marginRight: "8px" }} />
            {creatingPin ? "Đang tạo..." : "Tạo Xe & Pin"}
          </Button>
          <Button white blackoutline type="button" onClick={onClose}>
            Hủy
          </Button>
        </div>
      </div>
    </div>
  );
}

export default AddCarModal;
