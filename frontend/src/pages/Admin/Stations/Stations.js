import axios from "axios";
import React, { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faFilter,
  faPlus,
  faMapMarkerAlt,
  faEye,
  faEdit,
  faTimes,
  faDollarSign,
  faBatteryFull,
  faLocationDot,
} from "@fortawesome/free-solid-svg-icons";
import { faUser } from "@fortawesome/free-regular-svg-icons";
import styles from "./Stations.module.css";
import BatteryGrid from "../Batteries/modals/BatteryGrid"; // ✅ tái sử dụng file cũ

// ===== KPI đầu trang =====
const topKpi = [
  {
    title: "Tổng Doanh Thu",
    value: "$267.000",
    sub: "+12.5%",
    icon: faDollarSign,
    color: "#16a34a",
  },
  {
    title: "Tổng Lần Thay Pin",
    value: "12.847",
    sub: "+8.3%",
    icon: faBatteryFull,
    color: "#3b82f6",
  },
  {
    title: "Trạm Hoạt Động",
    value: "24",
    sub: "Tất Cả Trực Tuyến",
    icon: faLocationDot,
    color: "#a855f7",
  },
  {
    title: "Khách Hàng",
    value: "8.547",
    sub: "+156 mới",
    icon: faUser,
    color: "#f97316",
  },
];

export default function Stations() {
  const [stations, setStations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState("add");
  const [selectedStation, setSelectedStation] = useState(null);

  const [formData, setFormData] = useState({
    tenTram: "",
    diaChi: "",
    kinhDo: "",
    viDo: "",
    soLuongPinToiDa: "",
    soDT: "",
    trangThai: "Hoạt động",
  });

  const [showBatteryGrid, setShowBatteryGrid] = useState(false);
  const [selectedStationId, setSelectedStationId] = useState(null);

  // ===== VALIDATION =====
  const validatePhone = (phone) => {
    if (!phone) return "❌ Số điện thoại không được để trống.";
    if (/[a-zA-Z]/.test(phone)) return "❌ Số điện thoại chứa chữ cái.";
    if (/[^0-9]/.test(phone)) return "❌ Số điện thoại chứa ký tự đặc biệt.";
    if (phone.length < 10) return "❌ Số điện thoại nhỏ hơn 10 ký tự.";
    if (phone.length > 11) return "❌ Số điện thoại lớn hơn 11 ký tự.";
    return null;
  };

  const validateKinhDo = (value) => {
    if (value === "" || value === null || value === undefined)
      return "❌ Kinh độ sai kiểu dữ liệu.";

    if (/[a-zA-Z]/.test(value)) return "❌ Kinh độ sai kiểu dữ liệu.";

    const num = Number(value);

    if (Number.isNaN(num)) return "❌ Kinh độ sai kiểu dữ liệu.";

    if (num < -180) return "❌ Kinh độ nhỏ hơn -180.";

    if (num > 180) return "❌ Kinh độ lớn hơn 180.";

    return null;
  };

  const validateViDo = (value) => {
    if (value === "" || value === null || value === undefined)
      return "❌ Vĩ độ sai kiểu dữ liệu.";

    if (/[a-zA-Z]/.test(value)) return "❌ Vĩ độ sai kiểu dữ liệu.";

    const num = Number(value);

    if (Number.isNaN(num)) return "❌ Vĩ độ sai kiểu dữ liệu.";

    if (num < -90) return "❌ Vĩ độ nhỏ hơn -90.";

    if (num > 90) return "❌ Vĩ độ lớn hơn 90.";

    return null;
  };

  // ===== FETCH =====
  useEffect(() => {
    const fetchStations = async () => {
      try {
        const res = await axios.get("/api/station-service/tram");
        setStations(res.data);
      } catch (err) {
        setError("Không thể tải danh sách trạm");
      } finally {
        setLoading(false);
      }
    };
    fetchStations();
  }, []);

  // ===== MODAL =====
  const openModal = (mode, station = null) => {
    setModalMode(mode);
    if (station) {
      setSelectedStation(station);
      setFormData(station);
    } else {
      setFormData({
        tenTram: "",
        diaChi: "",
        kinhDo: "",
        viDo: "",
        soLuongPinToiDa: "",
        soDT: "",
        trangThai: "Hoạt động",
      });
    }
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedStation(null);
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // ===== SUBMIT =====
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.diaChi.length > 250) return alert("❌ Địa chỉ lố 250 kí tự.");

    if (formData.tenTram.length > 150)
      return alert("❌ Tên trạm lố 150 kí tự.");

    const phoneError = validatePhone(formData.soDT);
    if (phoneError) return alert(phoneError);

    const kinDoError = validateKinhDo(formData.kinhDo);
    if (kinDoError) return alert(kinDoError);

    const viDoError = validateViDo(formData.viDo);
    if (viDoError) return alert(viDoError);

    // convert trước khi gửi API
    const payload = {
      ...formData,
      kinhDo: Number(formData.kinhDo),
      viDo: Number(formData.viDo),
      soLuongPinToiDa: Number(formData.soLuongPinToiDa),
    };

    try {
      if (modalMode === "add") {
        const res = await axios.post("/api/station-service/tram", payload);
        setStations((prev) => [...prev, res.data]);
        alert("✅ Thêm trạm thành công!");
      } else {
        const res = await axios.put(
          `/api/station-service/tram/${selectedStation.maTram}`,
          payload,
        );

        setStations((prev) =>
          prev.map((st) =>
            st.maTram === selectedStation.maTram ? res.data : st,
          ),
        );

        alert("✅ Cập nhật trạm thành công!");
      }

      setShowModal(false);
    } catch (err) {
      alert(err?.response?.data?.message || "❌ Không thể lưu dữ liệu.");
    }
  };

  if (loading) return <p>Đang tải dữ liệu...</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;
  return (
    <div className={styles.wrapper}>
      {/* ===== KPI Section ===== */}
      <div className={styles.kpiGrid}>
        {topKpi.map((item, i) => (
          <div key={i} className={styles.kpiCard}>
            <div className={styles.kpiInfo}>
              <p className={styles.kpiTitle}>{item.title}</p>
              <h2 className={styles.kpiValue}>{item.value}</h2>
              <p className={styles.kpiSub}>{item.sub}</p>
            </div>
            <div
              className={styles.kpiIcon}
              style={{
                color: item.color,
                backgroundColor: item.color + "20",
              }}
            >
              <FontAwesomeIcon icon={item.icon} />
            </div>
          </div>
        ))}
      </div>

      {/* ===== Header ===== */}
      <div className={styles.header}>
        <h2 className={styles.headerTitle}>Quản Lý Trạm</h2>
        <div className={styles.headerActions}>
          <button className={styles.filterBtn}>
            <FontAwesomeIcon icon={faFilter} /> Lọc
          </button>
          <button className={styles.addBtn} onClick={() => openModal("add")}>
            <FontAwesomeIcon icon={faPlus} /> Thêm Trạm
          </button>
        </div>
      </div>

      {/* ===== Danh sách trạm ===== */}
      <div className={styles.stationList}>
        {stations.map((st, i) => (
          <div
            key={i}
            className={styles.stationCard}
            onClick={() => {
              setSelectedStationId(st.maTram);
              setShowBatteryGrid(true);
            }}
            style={{ cursor: "pointer" }}
          >
            <div className={styles.infoRow}>
              <div className={styles.infoLeft}>
                <div className={styles.iconWrapper}>
                  <FontAwesomeIcon
                    icon={faMapMarkerAlt}
                    className={styles.icon}
                  />
                  <div
                    className={`${styles.status} ${
                      styles[
                        st.trangThai === "Hoạt động"
                          ? "active"
                          : st.trangThai === "Bảo trì"
                            ? "maintenance"
                            : "offline"
                      ]
                    }`}
                  >
                    {st.trangThai}
                  </div>
                </div>
                <div>
                  <h3 className={styles.stationName}>{st.tenTram}</h3>
                  <div className={styles.infoDetails}>
                    <div>
                      Lần Thay Pin: <span>{st.swaps ?? 0}</span>
                    </div>
                    <div>
                      Doanh Thu: <span>${st.revenue ?? 0}</span>
                    </div>
                    <div>
                      Sử Dụng: <span>{st.utilization ?? 0}%</span>
                    </div>
                  </div>
                </div>
              </div>

              <div className={styles.actionBtns}>
                <button
                  className={styles.iconBtn}
                  onClick={(e) => {
                    e.stopPropagation();
                    openModal("view", st);
                  }}
                >
                  <FontAwesomeIcon icon={faEye} />
                </button>
                <button
                  className={styles.iconBtn}
                  onClick={(e) => {
                    e.stopPropagation();
                    openModal("edit", st);
                  }}
                >
                  <FontAwesomeIcon icon={faEdit} />
                </button>
              </div>
            </div>

            <div className={styles.progressWrapper}>
              <div className={styles.progressInfo}>
                <span>Sử Dụng</span>
                <span>{st.utilization ?? 0}%</span>
              </div>
              <div className={styles.progressBar}>
                <div
                  className={styles.progressFill}
                  style={{ width: `${st.utilization ?? 0}%` }}
                ></div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* ===== Modal Thêm/Sửa Trạm ===== */}
      {showModal && (
        <div className={styles.modalOverlay} onClick={closeModal}>
          <div
            className={styles.modalContent}
            onClick={(e) => e.stopPropagation()}
          >
            <div className={styles.modalHeader}>
              <h3>
                {modalMode === "add" ? "Thêm Trạm Mới" : "Sửa Thông Tin Trạm"}
              </h3>
              <button className={styles.closeBtn} onClick={closeModal}>
                <FontAwesomeIcon icon={faTimes} />
              </button>
            </div>

            <form onSubmit={handleSubmit}>
              <div className={styles.modalBody}>
                {/* Hàng 1: Tên Trạm & Số Điện Thoại */}
                <div className={styles.gridRow}>
                  <div className={styles.formGroup}>
                    <label>Tên Trạm *</label>
                    <input
                      type="text"
                      name="tenTram"
                      placeholder="VD: Trạm sạc trung tâm..."
                      value={formData.tenTram}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div className={styles.formGroup}>
                    <label>Số Điện Thoại *</label>
                    <input
                      type="text"
                      name="soDT"
                      placeholder="VD: 0912345678"
                      value={formData.soDT}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                {/* Hàng 2: Kinh độ & Vĩ độ */}
                <div className={styles.gridRow}>
                  <div className={styles.formGroup}>
                    <label>Kinh Độ *</label>
                    <input
                      type="text"
                      name="kinhDo"
                      step="any"
                      placeholder="VD: 106.6821"
                      value={formData.kinhDo}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div className={styles.formGroup}>
                    <label>Vĩ Độ *</label>
                    <input
                      type="text"
                      name="viDo"
                      step="any"
                      placeholder="VD: 10.7626"
                      value={formData.viDo}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                {/* Hàng 3: Pin tối đa & Trạng thái */}
                <div className={styles.gridRow}>
                  <div className={styles.formGroup}>
                    <label>Số Lượng Pin Tối Đa *</label>
                    <input
                      type="number"
                      name="soLuongPinToiDa"
                      placeholder="VD: 50"
                      value={formData.soLuongPinToiDa}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div className={styles.formGroup}>
                    <label>Trạng Thái *</label>
                    <select
                      name="trangThai"
                      value={formData.trangThai}
                      onChange={handleChange}
                    >
                      <option value="Hoạt động">Hoạt động</option>
                      <option value="Bảo trì">Bảo trì</option>
                      <option value="Tạm dừng">Tạm dừng</option>
                    </select>
                  </div>
                </div>

                {/* Hàng 4: Địa chỉ (Trải dài hết 2 cột) */}
                <div className={styles.formGroup}>
                  <label>Địa Chỉ *</label>
                  <input
                    type="text"
                    name="diaChi"
                    placeholder="Nhập địa chỉ chi tiết của trạm..."
                    value={formData.diaChi}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              <div className={styles.modalActions}>
                <button
                  type="button"
                  className={styles.cancelBtn}
                  onClick={closeModal}
                >
                  Hủy
                </button>
                <button type="submit" className={styles.submitBtn}>
                  {modalMode === "add" ? "Thêm Trạm" : "Lưu Thay Đổi"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ===== Modal BatteryGrid ===== */}
      {showBatteryGrid && (
        <div
          className={styles.modalOverlay}
          onClick={(e) => {
            // Khi click vào overlay (không phải vào phần nội dung modal)
            if (e.target.classList.contains(styles.modalOverlay)) {
              setShowBatteryGrid(false);
            }
          }}
        >
          <div className={styles.modalContentLarge}>
            <div className={styles.modalHeader}>
              <h3>
                Pin tại{" "}
                {stations.find((s) => s.maTram === selectedStationId)
                  ?.tenTram || "Trạm"}
              </h3>
              <button
                className={styles.closeBtn}
                onClick={() => setShowBatteryGrid(false)}
              >
                <FontAwesomeIcon icon={faTimes} />
              </button>
            </div>
            <BatteryGrid stationId={selectedStationId} />
          </div>
        </div>
      )}
    </div>
  );
}
