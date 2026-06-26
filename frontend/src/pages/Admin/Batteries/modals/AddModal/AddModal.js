import React, { useEffect, useState } from "react";
import styles from "./AddModal.module.css";

export default function AddModal({
                                     open,
                                     onClose,
                                     onDone,
                                     context = "batteries",
                                     tramId = null,
                                 }) {
    const [loading, setLoading] = useState(true);
    const [pins, setPins] = useState([]);
    const [stations, setStations] = useState([]);
    const [showSuccess, setShowSuccess] = useState(false);
    const [errors, setErrors] = useState({});
    const [uniqueModels, setUniqueModels] = useState([]);
    const [isNewModel, setIsNewModel] = useState(false);

    const token = localStorage.getItem("token");
    const today = new Date().toISOString().split("T")[0];

    const [form, setForm] = useState({
        loaiPin: "",
        dungLuong: "",
        newTinhTrang: "đầy",
        trangThaiSoHuu: "sẵn sàng",
        newSucKhoe: "",
        maTram: tramId || "",
        ngayNhapKho: today,
        ngayBaoDuongGanNhat: "",
        logNote: "",
    });

    /* 🟢 Load dữ liệu model và trạm */
    useEffect(() => {
        if (!open) return;
        (async () => {
            try {
                setLoading(true);
                const [pinsRes, tramRes] = await Promise.all([
                    fetch("/api/battery-service/pins", {
                        headers: token ? { Authorization: `Bearer ${token}` } : {},
                    }),
                    fetch("/api/station-service/tram", {
                        headers: token ? { Authorization: `Bearer ${token}` } : {},
                    }),
                ]);

                const pinsData = pinsRes.ok ? await pinsRes.json() : [];
                const tramData = tramRes.ok ? await tramRes.json() : [];

                setPins(pinsData);

                // Lọc model duy nhất
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

                if (context === "batteries") {
                    // Hiển thị tất cả trạm
                    const uniqStations = [];
                    const seenTram = new Set();
                    tramData.forEach((t) => {
                        const name = (t.tenTram ?? t.ten_tram ?? "").trim();
                        if (name && !seenTram.has(name)) {
                            uniqStations.push(t);
                            seenTram.add(name);
                        }
                    });
                    setStations(uniqStations);
                } else if (context === "station" && tramId) {
                    // Chỉ 1 trạm cụ thể
                    const found = tramData.find(
                        (t) => Number(t.maTram ?? t.ma_tram) === Number(tramId),
                    );
                    if (found) {
                        setStations([found]);
                        setForm((f) => ({ ...f, maTram: found.maTram ?? found.ma_tram }));
                    }
                }
            } catch (err) {
                console.error("⚠️ Lỗi load dữ liệu:", err);
                setPins([]);
                setStations([]);
            } finally {
                setLoading(false);
            }
        })();
    }, [open]);

    /* 🟢 Khi đổi tình trạng → giới hạn trạng thái sở hữu */
    useEffect(() => {
        const tinhTrang = form.newTinhTrang;
        if (tinhTrang === "đầy") {
            setForm((f) => ({ ...f, trangThaiSoHuu: "sẵn sàng" }));
        } else if (["đang sạc", "bảo trì"].includes(tinhTrang)) {
            setForm((f) => ({ ...f, trangThaiSoHuu: "chưa sẵn sàng" }));
        }
    }, [form.newTinhTrang]);

    /* 🧠 Kiểm tra lỗi */
    const validate = (field, value) => {
        let msg = "";
        if (field === "newSucKhoe") {
            const v = Number(value);
            if (isNaN(v) || v < 0 || v > 100)
                msg = "Giá trị sức khỏe phải trong khoảng 0–100%";
        }
        if ((field === "ngayNhapKho" || field === "ngayBaoDuongGanNhat") && value) {
            if (value > today) msg = "Không được chọn ngày trong tương lai";
        }
        if (field === "loaiPin" && !value.trim())
            msg = "Vui lòng nhập hoặc chọn model";
        if (field === "dungLuong" && (!value || value <= 0))
            msg = "Vui lòng nhập dung lượng hợp lệ";
        if (field === "maTram" && context === "batteries" && !value)
            msg = "Vui lòng chọn trạm";
        return msg;
    };

    const update = (field, value) => {
        setForm((prev) => {
            const updated = { ...prev, [field]: value };

            // Khi chọn model
            if (field === "loaiPin") {
                const found = uniqueModels.find((m) => m.loaiPin === value);
                if (found) {
                    updated.dungLuong = found.dungLuong;
                    setIsNewModel(false);
                } else {
                    updated.dungLuong = "";
                    setIsNewModel(true);
                }
            }

            const msg = validate(field, value);
            setErrors((e) => ({ ...e, [field]: msg }));
            return updated;
        });
    };

    /* 🔹 Submit */
    const handleSubmit = async () => {
        // Validate all fields
        const newErr = {};
        Object.entries(form).forEach(([k, v]) => {
            const msg = validate(k, v);
            if (msg) newErr[k] = msg;
        });
        setErrors(newErr);
        if (Object.keys(newErr).length > 0) return;

        try {
            const tinhTrangMap = {
                đầy: "DAY",
                "đang sạc": "DANG_SAC",
                "bảo trì": "BAO_TRI",
            };
            const trangThaiSoHuuMap = {
                "sẵn sàng": "SAN_SANG",
                "chưa sẵn sàng": "CHUA_SAN_SANG",
                "đang vận chuyển": "DANG_VAN_CHUYEN",
            };

            const newPin = {
                loaiPin: form.loaiPin,
                dungLuong: Number(form.dungLuong),
                tinhTrang: tinhTrangMap[form.newTinhTrang],
                trangThaiSoHuu: trangThaiSoHuuMap[form.trangThaiSoHuu],
                sucKhoe: Number(form.newSucKhoe),
                ngayBaoDuongGanNhat: form.ngayBaoDuongGanNhat || null,
                ngayNhapKho: form.ngayNhapKho || today,
            };

            const res = await fetch("/api/battery-service/pins", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    ...(token ? { Authorization: `Bearer ${token}` } : {}),
                },
                body: JSON.stringify(newPin),
            });
            if (!res.ok) throw new Error("Không thể thêm pin mới");

            // Ghi log lịch sử
            const addedPin = await res.json();
            const historyBody = {
                hanhDong: form.logNote || "Thêm pin mới vào kho",
                maPin: Number(addedPin.maPin ?? addedPin.id),
                maTram: Number(form.maTram || tramId),
                ngayThayDoi: new Date().toISOString(),
            };

            const res2 = await fetch("/api/battery-service/lichsu-pin-tram", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    ...(token ? { Authorization: `Bearer ${token}` } : {}),
                },
                body: JSON.stringify(historyBody),
            });
            if (!res2.ok) throw new Error("Không thể ghi lịch sử");

            setShowSuccess(true);
            setTimeout(() => {
                setShowSuccess(false);
                onDone?.();
                onClose?.();
            }, 1500);
        } catch (err) {
            alert("❌ " + err.message);
            console.error(err);
        }
    };

    if (!open) return null;

    const tinhTrangOptions = ["đầy", "đang sạc", "bảo trì"];
    const getTrangThaiOptions = () => {
        if (form.newTinhTrang === "đầy") return ["sẵn sàng", "đang vận chuyển"];
        return ["chưa sẵn sàng", "đang vận chuyển"];
    };

    return (
        <div
            className={styles.overlay}
            onClick={(e) =>
                e.target.classList.contains(styles.overlay) && onClose?.()
            }
        >
            <div className={styles.modal}>
                <div className={styles.header}>
                    <h3>Thêm pin mới vào kho</h3>
                    <button className={styles.closeBtn} onClick={onClose}>
                        ✕
                    </button>
                </div>

                <div className={styles.body}>
                    {loading ? (
                        <p>Đang tải dữ liệu...</p>
                    ) : (
                        <>
                            {/* 1️⃣ Model */}
                            <div className={styles.formRow}>
                                <label>Model pin</label>
                                <input
                                    name="loaiPin"
                                    list="modelList"
                                    value={form.loaiPin}
                                    onChange={(e) => update("loaiPin", e.target.value)}
                                    placeholder="Nhập hoặc chọn model có sẵn"
                                    className={`${styles.input} ${
                                        errors.loaiPin ? styles.inputError : ""
                                    }`}
                                />
                                <datalist id="modelList">
                                    {uniqueModels.map((m) => (
                                        <option key={m.loaiPin} value={m.loaiPin} />
                                    ))}
                                </datalist>
                                {errors.loaiPin && (
                                    <small className={styles.errorMsg}>{errors.loaiPin}</small>
                                )}
                            </div>

                            {/* 2️⃣ Dung lượng */}
                            <div className={styles.formRow}>
                                <label>Dung lượng (kWh)</label>
                                <input
                                    name="dungLuong"
                                    type="number"
                                    value={form.dungLuong}
                                    onChange={(e) => update("dungLuong", e.target.value)}
                                    readOnly={!isNewModel}
                                    placeholder={
                                        isNewModel ? "Nhập dung lượng mới" : "Tự động theo model"
                                    }
                                    className={`${styles.input} ${
                                        errors.dungLuong ? styles.inputError : ""
                                    }`}
                                />
                                {errors.dungLuong && (
                                    <small className={styles.errorMsg}>{errors.dungLuong}</small>
                                )}
                            </div>

                            {/* 3️⃣ Tình trạng & Trạng thái sở hữu */}
                            <div className={styles.twoCols}>
                                <div className={styles.formRow}>
                                    <label>Tình trạng</label>
                                    <select
                                        name="newTinhTrang"
                                        value={form.newTinhTrang}
                                        onChange={(e) => update("newTinhTrang", e.target.value)}
                                        className={styles.input}
                                    >
                                        {tinhTrangOptions.map((t) => (
                                            <option key={t} value={t}>
                                                {t}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className={styles.formRow}>
                                    <label>Trạng thái sở hữu</label>
                                    <select
                                        name="trangThaiSoHuu"
                                        value={form.trangThaiSoHuu}
                                        onChange={(e) => update("trangThaiSoHuu", e.target.value)}
                                        className={styles.input}
                                    >
                                        {getTrangThaiOptions().map((t) => (
                                            <option key={t} value={t}>
                                                {t}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>

                            {/* 4️⃣ Sức khỏe */}
                            <div className={styles.formRow}>
                                <label>% Sức khỏe</label>
                                <input
                                    name="newSucKhoe"
                                    type="number"
                                    value={form.newSucKhoe}
                                    onChange={(e) => update("newSucKhoe", e.target.value)}
                                    placeholder="0–100"
                                    className={`${styles.input} ${
                                        errors.newSucKhoe ? styles.inputError : ""
                                    }`}
                                />
                                {errors.newSucKhoe && (
                                    <small className={styles.errorMsg}>{errors.newSucKhoe}</small>
                                )}
                            </div>

                            {/* 5️⃣ Ngày nhập kho + Ngày bảo dưỡng */}
                            <div className={styles.twoCols}>
                                <div className={styles.formRow}>
                                    <label>Ngày nhập kho</label>
                                    <input
                                        name="ngayNhapKho"
                                        type="date"
                                        value={form.ngayNhapKho}
                                        onChange={(e) => update("ngayNhapKho", e.target.value)}
                                        className={`${styles.input} ${
                                            errors.ngayNhapKho ? styles.inputError : ""
                                        }`}
                                    />
                                    {errors.ngayNhapKho && (
                                        <small className={styles.errorMsg}>
                                            {errors.ngayNhapKho}
                                        </small>
                                    )}
                                </div>
                                <div className={styles.formRow}>
                                    <label>Lần bảo dưỡng gần nhất</label>
                                    <input
                                        name="ngayBaoDuongGanNhat"
                                        type="date"
                                        value={form.ngayBaoDuongGanNhat}
                                        onChange={(e) =>
                                            update("ngayBaoDuongGanNhat", e.target.value)
                                        }
                                        className={`${styles.input} ${
                                            errors.ngayBaoDuongGanNhat ? styles.inputError : ""
                                        }`}
                                    />
                                    {errors.ngayBaoDuongGanNhat && (
                                        <small className={styles.errorMsg}>
                                            {errors.ngayBaoDuongGanNhat}
                                        </small>
                                    )}
                                </div>
                            </div>

                            {/* 6️⃣ Trạm */}
                            {context === "batteries" ? (
                                <div className={styles.formRow}>
                                    <label>Chọn trạm</label>
                                    <select
                                        name="maTram"
                                        value={form.maTram}
                                        onChange={(e) => update("maTram", e.target.value)}
                                        className={`${styles.input} ${
                                            errors.maTram ? styles.inputError : ""
                                        }`}
                                    >
                                        <option value="">-- Chọn trạm --</option>
                                        {stations.map((t) => (
                                            <option
                                                key={t.maTram ?? t.ma_tram}
                                                value={t.maTram ?? t.ma_tram}
                                            >
                                                {t.tenTram ?? t.ten_tram}
                                            </option>
                                        ))}
                                    </select>
                                    {errors.maTram && (
                                        <small className={styles.errorMsg}>{errors.maTram}</small>
                                    )}
                                </div>
                            ) : (
                                <div className={styles.formRow}>
                                    <label>Trạm hiện tại</label>
                                    <input
                                        type="text"
                                        value={stations[0]?.tenTram ?? "Trạm hiện tại"}
                                        readOnly
                                        className={styles.input}
                                    />
                                </div>
                            )}

                            {/* 7️⃣ Ghi chú */}
                            <div className={styles.formRow}>
                                <label>Ghi chú lịch sử</label>
                                <input
                                    name="logNote"
                                    type="text"
                                    value={form.logNote}
                                    onChange={(e) => update("logNote", e.target.value)}
                                    placeholder="VD: Nhập pin mới về kho"
                                    className={styles.input}
                                />
                            </div>
                        </>
                    )}
                </div>

                <div className={styles.footer}>
                    <button className={styles.secondaryBtn} onClick={onClose}>
                        Hủy
                    </button>
                    <button className={styles.primaryBtn} onClick={handleSubmit}>
                        Xác nhận
                    </button>
                </div>

                {showSuccess && (
                    <div className={styles.toast}>✅ Thêm pin mới thành công!</div>
                )}
            </div>
        </div>
    );
}
