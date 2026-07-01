using System;
using System.Collections.Generic;
using System.Drawing;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace InnovaTech.POS
{
    // ─── Modelo de ítem del carrito ───────────────────────────────────
    public class ItemCarrito
    {
        public string Sku { get; set; } = "";
        public string Descripcion { get; set; } = "";
        public int Cantidad { get; set; }
        public double PrecioUnitario { get; set; }
        public double Subtotal => Cantidad * PrecioUnitario;

        public override string ToString() =>
            $"[{Cantidad:D2}x]  {Sku,-14}  {Descripcion,-22}  S/ {PrecioUnitario,8:F2}  →  S/ {Subtotal,8:F2}";
    }

    // ─────────────────────────────────────────────────────────────────
    //  VENTANA: Registro de Venta en Sucursal (Proceso TO-BE 1)
    //  FASE 1: Escanear productos → carrito con precios del catálogo
    //  FASE 2: DNI → consulta servicio-clientes (si no existe: editable)
    //  FASE 3: Cobro → Efectivo (con vuelto) o Terminal Externo
    // ─────────────────────────────────────────────────────────────────
    public class FormVenta : Form
    {
        // FASE 1
        private TextBox txtSkuInput = null!;
        private TextBox txtCantidad = null!;
        private Button btnAgregarCarrito = null!;
        private ListBox lstCarrito = null!;
        private Label lblTotal = null!;
        private Button btnLimpiarCarrito = null!;

        // FASE 2
        private TextBox txtDni = null!;
        private Button btnConsultarDni = null!;
        private TextBox txtNombreCliente = null!;

        // FASE 3
        private RadioButton rbEfectivo = null!;
        private RadioButton rbTarjeta = null!;
        private Panel pnlEfectivo = null!;
        private TextBox txtMontoRecibido = null!;
        private Label lblVuelto = null!;
        private Panel pnlTarjeta = null!;
        private Button btnTerminalExterno = null!;
        private Label lblAuthCode = null!;
        private Button btnCobrar = null!;

        // Consola
        private RichTextBox txtLog = null!;

        // Estado
        private readonly List<ItemCarrito> _carrito = new();
        private double _totalCarrito = 0;
        private string _authCode = "";

        public FormVenta()
        {
            ConfigurarUI();
        }

        private void ConfigurarUI()
        {
            this.Text = "InnovaTech POS — Registrar Venta";
            this.Size = new Size(630, 830);
            this.BackColor = Color.FromArgb(20, 25, 40);
            this.StartPosition = FormStartPosition.CenterScreen;
            this.FormBorderStyle = FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;

            int y = 15;

            // ── Encabezado ────────────────────────────────────────────
            Lbl("🛒 REGISTRAR VENTA", 15, Color.White, 165, y, true); y += 30;
            Lbl($"{Form1.SEDE_ID}  |  {Form1.CAJA_ID}", 9, Color.FromArgb(120, 180, 255), 205, y); y += 35;

            // ═════════════════════════════════════════════════════════
            // FASE 1 — CARRITO
            // ═════════════════════════════════════════════════════════
            Lbl("1.  Escanear Productos", 10, Color.FromArgb(255, 200, 60), 20, y, true); y += 25;

            Lbl("SKU:", 9, Color.FromArgb(180, 190, 210), 20, y + 4);
            txtSkuInput = TB(70, y, 205, "SKU-LAP-001");

            Lbl("Cant:", 9, Color.FromArgb(180, 190, 210), 288, y + 4);
            txtCantidad = TB(330, y, 55, "1");

            btnAgregarCarrito = Btn("＋ Agregar", 400, y - 1, 180, 30, Color.FromArgb(30, 100, 180));
            btnAgregarCarrito.Click += BtnAgregarCarrito_Click;
            y += 40;

            lstCarrito = new ListBox()
            {
                Location = new Point(20, y),
                Size = new Size(575, 145),
                BackColor = Color.FromArgb(28, 33, 50),
                ForeColor = Color.FromArgb(200, 220, 255),
                Font = new Font("Consolas", 9),
                BorderStyle = BorderStyle.None
            };
            this.Controls.Add(lstCarrito);
            y += 153;

            btnLimpiarCarrito = Btn("🗑 Limpiar carrito", 20, y, 150, 26, Color.FromArgb(100, 40, 40));
            btnLimpiarCarrito.Font = new Font("Segoe UI", 8, FontStyle.Bold);
            btnLimpiarCarrito.Click += (s, e) => LimpiarCarrito();

            lblTotal = new Label()
            {
                Text = "Total:  S/ 0.00",
                Font = new Font("Segoe UI", 13, FontStyle.Bold),
                ForeColor = Color.FromArgb(0, 230, 120),
                Location = new Point(370, y),
                AutoSize = true
            };
            this.Controls.Add(lblTotal);
            y += 40;

            // ═════════════════════════════════════════════════════════
            // FASE 2 — CLIENTE
            // ═════════════════════════════════════════════════════════
            Separador(y); y += 14;
            Lbl("2.  Datos del Cliente", 10, Color.FromArgb(255, 200, 60), 20, y, true); y += 26;

            Lbl("DNI:", 9, Color.FromArgb(180, 190, 210), 20, y + 4);
            txtDni = TB(65, y, 145, "72019999");

            btnConsultarDni = Btn("Consultar", 225, y - 1, 105, 30, Color.FromArgb(60, 90, 140));
            btnConsultarDni.Click += BtnConsultarDni_Click;

            Lbl("Nombre:", 9, Color.FromArgb(180, 190, 210), 345, y + 4);
            txtNombreCliente = TB(410, y, 175, "");
            txtNombreCliente.PlaceholderText = "Sin datos / Ingresar";
            y += 45;

            // ═════════════════════════════════════════════════════════
            // FASE 3 — COBRO
            // ═════════════════════════════════════════════════════════
            Separador(y); y += 14;
            Lbl("3.  Medio de Pago", 10, Color.FromArgb(255, 200, 60), 20, y, true); y += 26;

            rbEfectivo = new RadioButton()
            {
                Text = "💵  Efectivo", Location = new Point(20, y),
                AutoSize = true, Font = new Font("Segoe UI", 10),
                ForeColor = Color.White, Checked = true
            };
            rbEfectivo.CheckedChanged += MedioPago_Changed;
            this.Controls.Add(rbEfectivo);

            rbTarjeta = new RadioButton()
            {
                Text = "💳  Tarjeta", Location = new Point(165, y),
                AutoSize = true, Font = new Font("Segoe UI", 10),
                ForeColor = Color.White
            };
            rbTarjeta.CheckedChanged += MedioPago_Changed;
            this.Controls.Add(rbTarjeta);
            y += 35;

            // Panel Efectivo ──────────────────────────────────────────
            pnlEfectivo = new Panel()
            {
                Location = new Point(20, y), Size = new Size(575, 35),
                BackColor = Color.Transparent
            };
            var lE = new Label() { Text = "Monto recibido (S/):", Font = new Font("Segoe UI", 9), ForeColor = Color.FromArgb(180, 190, 210), Location = new Point(0, 7), AutoSize = true };
            pnlEfectivo.Controls.Add(lE);
            txtMontoRecibido = new TextBox() { Location = new Point(148, 3), Width = 100, BackColor = Color.FromArgb(35, 40, 60), ForeColor = Color.White, Font = new Font("Segoe UI", 10), Text = "0.00" };
            txtMontoRecibido.TextChanged += TxtMontoRecibido_TextChanged;
            pnlEfectivo.Controls.Add(txtMontoRecibido);
            lblVuelto = new Label() { Text = "Vuelto: S/ 0.00", Font = new Font("Segoe UI", 10, FontStyle.Bold), ForeColor = Color.FromArgb(255, 200, 60), Location = new Point(265, 6), AutoSize = true };
            pnlEfectivo.Controls.Add(lblVuelto);
            this.Controls.Add(pnlEfectivo);

            // Panel Tarjeta ───────────────────────────────────────────
            pnlTarjeta = new Panel()
            {
                Location = new Point(20, y), Size = new Size(575, 35),
                BackColor = Color.Transparent, Visible = false
            };
            btnTerminalExterno = new Button()
            {
                Text = "Terminal Externo", Location = new Point(0, 2), Size = new Size(165, 30),
                Font = new Font("Segoe UI", 9, FontStyle.Bold),
                BackColor = Color.FromArgb(100, 60, 160), ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat, Cursor = Cursors.Hand
            };
            btnTerminalExterno.FlatAppearance.BorderSize = 0;
            btnTerminalExterno.Click += BtnTerminalExterno_Click;
            pnlTarjeta.Controls.Add(btnTerminalExterno);
            lblAuthCode = new Label() { Text = "", Font = new Font("Consolas", 10, FontStyle.Bold), ForeColor = Color.FromArgb(0, 230, 120), Location = new Point(180, 6), AutoSize = true };
            pnlTarjeta.Controls.Add(lblAuthCode);
            this.Controls.Add(pnlTarjeta);
            y += 48;

            // Botón cobrar ────────────────────────────────────────────
            btnCobrar = Btn("✅  PROCESAR COBRO", 160, y, 280, 50, Color.FromArgb(34, 139, 34));
            btnCobrar.Font = new Font("Segoe UI", 12, FontStyle.Bold);
            btnCobrar.Click += BtnCobrar_Click;
            y += 62;

            // Consola ─────────────────────────────────────────────────
            Lbl("Respuesta del servidor:", 9, Color.FromArgb(120, 180, 255), 20, y, true); y += 20;
            txtLog = new RichTextBox()
            {
                Location = new Point(20, y), Size = new Size(575, 120),
                ReadOnly = true, BackColor = Color.FromArgb(10, 12, 20),
                ForeColor = Color.FromArgb(0, 230, 120), Font = new Font("Consolas", 9),
                BorderStyle = BorderStyle.None
            };
            this.Controls.Add(txtLog);
        }

        // ── Helpers UI ────────────────────────────────────────────────
        private Label Lbl(string t, int fs, Color c, int x, int y, bool bold = false)
        {
            var l = new Label() { Text = t, Font = new Font("Segoe UI", fs, bold ? FontStyle.Bold : FontStyle.Regular), ForeColor = c, Location = new Point(x, y), AutoSize = true };
            this.Controls.Add(l); return l;
        }
        private TextBox TB(int x, int y, int w, string t)
        {
            var tb = new TextBox() { Location = new Point(x, y), Width = w, Text = t, BackColor = Color.FromArgb(35, 40, 60), ForeColor = Color.White, BorderStyle = BorderStyle.FixedSingle, Font = new Font("Segoe UI", 10) };
            this.Controls.Add(tb); return tb;
        }
        private Button Btn(string t, int x, int y, int w, int h, Color bg)
        {
            var b = new Button() { Text = t, Location = new Point(x, y), Size = new Size(w, h), BackColor = bg, ForeColor = Color.White, FlatStyle = FlatStyle.Flat, Cursor = Cursors.Hand, Font = new Font("Segoe UI", 9, FontStyle.Bold) };
            b.FlatAppearance.BorderSize = 0; this.Controls.Add(b); return b;
        }
        private void Separador(int y)
        {
            var p = new Panel() { Location = new Point(20, y), Size = new Size(575, 1), BackColor = Color.FromArgb(50, 60, 90) };
            this.Controls.Add(p);
        }

        // ── Limpiar carrito ───────────────────────────────────────────
        private void LimpiarCarrito()
        {
            _carrito.Clear();
            lstCarrito.Items.Clear();
            _totalCarrito = 0;
            lblTotal.Text = "Total:  S/ 0.00";
        }

        // ── FASE 1: Agregar al carrito → consulta servicio-catalogo ──
        private async void BtnAgregarCarrito_Click(object? sender, EventArgs e)
        {
            string sku = txtSkuInput.Text.Trim();
            if (!int.TryParse(txtCantidad.Text.Trim(), out int cantidad) || cantidad <= 0 || string.IsNullOrEmpty(sku))
            {
                MessageBox.Show("Ingrese un SKU válido y una cantidad mayor a cero.", "Validación", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            btnAgregarCarrito.Enabled = false;
            btnAgregarCarrito.Text = "Buscando...";

            try
            {
                using HttpClient client = new() { Timeout = TimeSpan.FromSeconds(5) };
                var resp = await client.GetAsync($"{Form1.GATEWAY_BASE}/api/v1/catalogo/productos/{sku}");

                if (resp.IsSuccessStatusCode)
                {
                    string json = await resp.Content.ReadAsStringAsync();
                    using JsonDocument doc = JsonDocument.Parse(json);
                    var root = doc.RootElement;
                    string desc = root.GetProperty("detalles").GetProperty("descripcion").GetString() ?? sku;
                    double precio = root.GetProperty("precios").GetProperty("precioBase").GetDouble();

                    AgregarItemAlCarrito(sku, desc, cantidad, precio);
                }
                else
                {
                    // SKU no registrado en el catálogo — se reporta en consola, no se agrega
                    txtLog.SelectionColor = Color.FromArgb(255, 160, 0);
                    txtLog.AppendText($"⚠ SKU '{sku}' no existe en el catálogo. Verifique el código.\n");
                }
            }
            catch
            {
                // Sin conexión: agrega igual para no bloquear la demo
                AgregarItemAlCarrito(sku, "(sin conexión)", cantidad, 0.00);
                MessageBox.Show("Sin conexión al catálogo. Artículo agregado con precio 0.00.", "Aviso", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }

            btnAgregarCarrito.Enabled = true;
            btnAgregarCarrito.Text = "＋ Agregar";
        }

        private void AgregarItemAlCarrito(string sku, string desc, int cantidad, double precio)
        {
            var item = new ItemCarrito { Sku = sku, Descripcion = desc, Cantidad = cantidad, PrecioUnitario = precio };
            _carrito.Add(item);
            lstCarrito.Items.Add(item.ToString());
            _totalCarrito += item.Subtotal;
            lblTotal.Text = $"Total:  S/ {_totalCarrito:F2}";
            txtSkuInput.Clear();
            txtCantidad.Text = "1";
            txtSkuInput.Focus();
        }

        // ── FASE 2: Consultar DNI → servicio-clientes ─────────────────
        private async void BtnConsultarDni_Click(object? sender, EventArgs e)
        {
            string dni = txtDni.Text.Trim();
            if (string.IsNullOrEmpty(dni)) return;

            btnConsultarDni.Enabled = false;
            txtNombreCliente.Text = "Consultando...";
            txtNombreCliente.ReadOnly = true;

            try
            {
                using HttpClient client = new() { Timeout = TimeSpan.FromSeconds(5) };
                var resp = await client.GetAsync($"{Form1.GATEWAY_BASE}/api/v1/clientes/{dni}");

                if (resp.IsSuccessStatusCode)
                {
                    string json = await resp.Content.ReadAsStringAsync();
                    using JsonDocument doc = JsonDocument.Parse(json);
                    txtNombreCliente.Text = doc.RootElement.GetProperty("nombreCompleto").GetString() ?? "";
                    txtNombreCliente.ReadOnly = true;
                }
                else
                {
                    // No existe: deja editable para que el cajero ingrese el nombre
                    txtNombreCliente.Text = "";
                    txtNombreCliente.ReadOnly = false;
                    txtNombreCliente.Focus();
                }
            }
            catch
            {
                txtNombreCliente.Text = "";
                txtNombreCliente.ReadOnly = false;
            }

            btnConsultarDni.Enabled = true;
        }

        // ── FASE 3: Cambiar panel de pago ─────────────────────────────
        private void MedioPago_Changed(object? sender, EventArgs e)
        {
            pnlEfectivo.Visible = rbEfectivo.Checked;
            pnlTarjeta.Visible = rbTarjeta.Checked;
            _authCode = "";
            lblAuthCode.Text = "";
        }

        // ── FASE 3: Calcular vuelto en efectivo ───────────────────────
        private void TxtMontoRecibido_TextChanged(object? sender, EventArgs e)
        {
            if (double.TryParse(txtMontoRecibido.Text, out double recibido))
            {
                double vuelto = recibido - _totalCarrito;
                lblVuelto.Text = $"Vuelto: S/ {Math.Max(vuelto, 0):F2}";
                lblVuelto.ForeColor = vuelto >= 0
                    ? Color.FromArgb(255, 200, 60)
                    : Color.FromArgb(255, 80, 80);
            }
        }

        // ── FASE 3: Terminal externo (hardware POS físico simulado) ───
        private async void BtnTerminalExterno_Click(object? sender, EventArgs e)
        {
            btnTerminalExterno.Enabled = false;
            btnTerminalExterno.Text = "Procesando...";
            lblAuthCode.Text = "";
            await Task.Delay(2000); // Simula tiempo de respuesta del terminal físico
            _authCode = $"AUTH-{new Random().Next(100000, 999999)}";
            lblAuthCode.Text = $"✅ {_authCode}";
            btnTerminalExterno.Text = "Terminal Externo";
            btnTerminalExterno.Enabled = true;
        }

        // ── FASE 3: Enviar cobro → XML → WSO2 → servicio-ventapos ────
        private async void BtnCobrar_Click(object? sender, EventArgs e)
        {
            if (_carrito.Count == 0)
            {
                MessageBox.Show("El carrito está vacío.", "Aviso", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }
            if (rbTarjeta.Checked && string.IsNullOrEmpty(_authCode))
            {
                MessageBox.Show("Primero confirme el pago con el Terminal Externo.", "Aviso", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            btnCobrar.Enabled = false;
            btnCobrar.Text = "Procesando...";
            txtLog.Clear();

            // Ensamblar artículos
            var artXml = new StringBuilder();
            foreach (var item in _carrito)
                artXml.AppendLine($"        <articulo><codigoArticulo>{item.Sku}</codigoArticulo><cantidad>{item.Cantidad}</cantidad><precioUnitario>{item.PrecioUnitario:F2}</precioUnitario></articulo>");

            string medioPago = rbTarjeta.Checked ? "TARJETA" : "EFECTIVO";
            string xmlPayload = $@"<PosOrder>
    <sedeId>{Form1.SEDE_ID}</sedeId>
    <cajaId>{Form1.CAJA_ID}</cajaId>
    <dniCliente>{txtDni.Text.Trim()}</dniCliente>
    <nombreCliente>{txtNombreCliente.Text.Trim()}</nombreCliente>
    <medioPago>{medioPago}</medioPago>
    <tokenTarjeta>{_authCode}</tokenTarjeta>
    <montoTotal>{_totalCarrito:F2}</montoTotal>
    <articulos>
{artXml}    </articulos>
</PosOrder>";

            txtLog.AppendText($"[1] Trama XML lista ({_carrito.Count} artículo(s), S/ {_totalCarrito:F2}).\n");
            txtLog.AppendText($"[2] Enviando a WSO2 → servicio-ventapos...\n");

            try
            {
                using HttpClient client = new() { Timeout = TimeSpan.FromSeconds(10) };
                var content = new StringContent(xmlPayload, Encoding.UTF8, "application/xml");
                var response = await client.PostAsync($"{Form1.WSO2_BASE}/wso2/pos/checkout", content);
                string result = await response.Content.ReadAsStringAsync();

                if (response.IsSuccessStatusCode)
                {
                    txtLog.SelectionColor = Color.FromArgb(0, 230, 120);
                    txtLog.AppendText("\n✅ VENTA REGISTRADA\n" + result + "\n");
                    // Limpiar todo tras éxito
                    LimpiarCarrito();
                    txtDni.Clear();
                    txtNombreCliente.Text = "";
                    txtNombreCliente.ReadOnly = false;
                    _authCode = "";
                    lblAuthCode.Text = "";
                }
                else
                {
                    txtLog.SelectionColor = Color.FromArgb(255, 80, 80);
                    txtLog.AppendText($"\n❌ ERROR HTTP {(int)response.StatusCode}\n{result}\n");
                }
            }
            catch (TaskCanceledException)
            {
                txtLog.SelectionColor = Color.FromArgb(255, 200, 0);
                txtLog.AppendText("\n⏱ TIMEOUT — Continúe en modo manual.\n");
            }
            catch (Exception ex)
            {
                txtLog.SelectionColor = Color.FromArgb(255, 80, 80);
                txtLog.AppendText("\n❌ ERROR DE RED:\n" + ex.Message);
            }

            btnCobrar.Enabled = true;
            btnCobrar.Text = "✅  PROCESAR COBRO";
        }
    }
}
