using System;
using System.Collections.Generic;
using System.Drawing;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace InnovaTech.POS
{
    // ─────────────────────────────────────────────────────────────
    //  VENTANA: Recepción de Mercadería en Sucursal (Proceso TO-BE 6)
    //  Flujo: Cajero pistolea SKUs → arma lote → lo envía a WSO2
    //         → servicio-inventario suma stock a la sede
    // ─────────────────────────────────────────────────────────────
    public class FormRecepcion : Form
    {
        private TextBox txtSkuInput = null!;
        private TextBox txtCantidadInput = null!;
        private ListBox lstLote = null!;
        private RichTextBox txtLog = null!;
        private Button btnAgregarSku = null!;
        private Button btnEnviarLote = null!;
        private Button btnLimpiar = null!;
        private Label lblContador = null!;

        // Lista de artículos en el lote actual
        private readonly List<(string sku, int cantidad)> _lote = new();

        public FormRecepcion()
        {
            ConfigurarUI();
        }

        private void ConfigurarUI()
        {
            this.Text = "InnovaTech POS — Recibir Mercadería";
            this.Size = new Size(560, 700);
            this.BackColor = Color.FromArgb(20, 25, 40);
            this.StartPosition = FormStartPosition.CenterScreen;
            this.FormBorderStyle = FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;

            // ─── Encabezado ───────────────────────────────────────────
            Label lblTitulo = new Label()
            {
                Text = "📦 RECIBIR MERCADERÍA",
                Font = new Font("Segoe UI", 16, FontStyle.Bold),
                ForeColor = Color.White,
                Location = new Point(120, 20),
                AutoSize = true
            };
            this.Controls.Add(lblTitulo);

            Label lblSede = new Label()
            {
                Text = $"Destino: {Form1.SEDE_ID}",
                Font = new Font("Segoe UI", 9),
                ForeColor = Color.FromArgb(120, 180, 255),
                Location = new Point(185, 52),
                AutoSize = true
            };
            this.Controls.Add(lblSede);

            // ─── Sección: Agregar producto al lote ────────────────────
            Label lblSeccion1 = new Label()
            {
                Text = "1. Pistolear / Ingresar SKU al lote:",
                Font = new Font("Segoe UI", 9, FontStyle.Bold),
                ForeColor = Color.FromArgb(255, 200, 60),
                Location = new Point(30, 90),
                AutoSize = true
            };
            this.Controls.Add(lblSeccion1);

            Label lblSku = new Label()
            {
                Text = "SKU:",
                Font = new Font("Segoe UI", 9),
                ForeColor = Color.FromArgb(180, 190, 210),
                Location = new Point(30, 118),
                AutoSize = true
            };
            this.Controls.Add(lblSku);

            txtSkuInput = new TextBox()
            {
                Location = new Point(80, 115),
                Width = 190,
                BackColor = Color.FromArgb(35, 40, 60),
                ForeColor = Color.White,
                BorderStyle = BorderStyle.FixedSingle,
                Font = new Font("Segoe UI", 10),
                Text = "SKU-LAP-001"
            };
            this.Controls.Add(txtSkuInput);

            Label lblCant = new Label()
            {
                Text = "Cant:",
                Font = new Font("Segoe UI", 9),
                ForeColor = Color.FromArgb(180, 190, 210),
                Location = new Point(283, 118),
                AutoSize = true
            };
            this.Controls.Add(lblCant);

            txtCantidadInput = new TextBox()
            {
                Location = new Point(325, 115),
                Width = 60,
                BackColor = Color.FromArgb(35, 40, 60),
                ForeColor = Color.White,
                BorderStyle = BorderStyle.FixedSingle,
                Font = new Font("Segoe UI", 10),
                Text = "1"
            };
            this.Controls.Add(txtCantidadInput);

            btnAgregarSku = new Button()
            {
                Text = "＋ Agregar",
                Location = new Point(400, 113),
                Size = new Size(110, 30),
                Font = new Font("Segoe UI", 9, FontStyle.Bold),
                BackColor = Color.FromArgb(30, 100, 180),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnAgregarSku.FlatAppearance.BorderSize = 0;
            btnAgregarSku.Click += BtnAgregarSku_Click;
            this.Controls.Add(btnAgregarSku);

            // ─── Lista del lote ───────────────────────────────────────
            Label lblSeccion2 = new Label()
            {
                Text = "2. Lote armado:",
                Font = new Font("Segoe UI", 9, FontStyle.Bold),
                ForeColor = Color.FromArgb(255, 200, 60),
                Location = new Point(30, 160),
                AutoSize = true
            };
            this.Controls.Add(lblSeccion2);

            lstLote = new ListBox()
            {
                Location = new Point(30, 183),
                Size = new Size(480, 160),
                BackColor = Color.FromArgb(28, 33, 50),
                ForeColor = Color.FromArgb(200, 220, 255),
                Font = new Font("Consolas", 10),
                BorderStyle = BorderStyle.None
            };
            this.Controls.Add(lstLote);

            lblContador = new Label()
            {
                Text = "0 artículo(s) en el lote",
                Font = new Font("Segoe UI", 8),
                ForeColor = Color.FromArgb(100, 120, 160),
                Location = new Point(30, 350),
                AutoSize = true
            };
            this.Controls.Add(lblContador);

            // ─── Botón limpiar lote ───────────────────────────────────
            btnLimpiar = new Button()
            {
                Text = "🗑 Limpiar lote",
                Location = new Point(350, 347),
                Size = new Size(160, 28),
                Font = new Font("Segoe UI", 8),
                BackColor = Color.FromArgb(100, 40, 40),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnLimpiar.FlatAppearance.BorderSize = 0;
            btnLimpiar.Click += BtnLimpiar_Click;
            this.Controls.Add(btnLimpiar);

            // ─── Botón enviar lote ────────────────────────────────────
            btnEnviarLote = new Button()
            {
                Text = "📤  REGISTRAR INGRESO DE LOTE",
                Location = new Point(100, 390),
                Size = new Size(340, 50),
                Font = new Font("Segoe UI", 11, FontStyle.Bold),
                BackColor = Color.FromArgb(34, 139, 34),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnEnviarLote.FlatAppearance.BorderSize = 0;
            btnEnviarLote.Click += BtnEnviarLote_Click;
            this.Controls.Add(btnEnviarLote);

            // ─── Consola ──────────────────────────────────────────────
            Label lblConsola = new Label()
            {
                Text = "Respuesta del Servidor:",
                Font = new Font("Segoe UI", 9, FontStyle.Bold),
                ForeColor = Color.FromArgb(120, 180, 255),
                Location = new Point(30, 458),
                AutoSize = true
            };
            this.Controls.Add(lblConsola);

            txtLog = new RichTextBox()
            {
                Location = new Point(30, 480),
                Size = new Size(480, 170),
                ReadOnly = true,
                BackColor = Color.FromArgb(10, 12, 20),
                ForeColor = Color.FromArgb(0, 230, 120),
                Font = new Font("Consolas", 9),
                BorderStyle = BorderStyle.None
            };
            this.Controls.Add(txtLog);
        }

        // ── Agrega un artículo al lote en memoria ─────────────────────
        private void BtnAgregarSku_Click(object? sender, EventArgs e)
        {
            string sku = txtSkuInput.Text.Trim();
            if (!int.TryParse(txtCantidadInput.Text.Trim(), out int cantidad) || cantidad <= 0)
            {
                MessageBox.Show("Cantidad inválida.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }
            if (string.IsNullOrEmpty(sku))
            {
                MessageBox.Show("Ingrese un SKU.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            _lote.Add((sku, cantidad));
            lstLote.Items.Add($"  [{cantidad:D3}]  {sku}");
            lblContador.Text = $"{_lote.Count} artículo(s) en el lote";
            txtSkuInput.Clear();
            txtCantidadInput.Text = "1";
            txtSkuInput.Focus();
        }

        // ── Limpia el lote ────────────────────────────────────────────
        private void BtnLimpiar_Click(object? sender, EventArgs e)
        {
            _lote.Clear();
            lstLote.Items.Clear();
            lblContador.Text = "0 artículo(s) en el lote";
            txtLog.Clear();
        }

        // ── Envía el lote completo a WSO2 → servicio-inventario ───────
        private async void BtnEnviarLote_Click(object? sender, EventArgs e)
        {
            if (_lote.Count == 0)
            {
                MessageBox.Show("No hay artículos en el lote.", "Lote vacío", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            btnEnviarLote.Enabled = false;
            btnEnviarLote.Text = "Registrando...";
            txtLog.Clear();

            // ── Ensamblar XML del lote ────────────────────────────────
            txtLog.AppendText($"[1] Armando lote de {_lote.Count} artículo(s)...\n");

            StringBuilder articulosXml = new StringBuilder();
            foreach (var (sku, cantidad) in _lote)
            {
                articulosXml.AppendLine($"        <articulo>");
                articulosXml.AppendLine($"            <codigoArticulo>{sku}</codigoArticulo>");
                articulosXml.AppendLine($"            <cantidad>{cantidad}</cantidad>");
                articulosXml.AppendLine($"        </articulo>");
            }

            string xmlPayload = $@"<IngresoLote>
    <sedeId>{Form1.SEDE_ID}</sedeId>
    <cajaId>{Form1.CAJA_ID}</cajaId>
    <articulos>
{articulosXml}    </articulos>
</IngresoLote>";

            // ── Enviar a WSO2 ─────────────────────────────────────────
            txtLog.AppendText($"[2] Enviando lote a WSO2 ({Form1.WSO2_BASE})...\n");

            try
            {
                using (HttpClient client = new HttpClient())
                {
                    client.Timeout = TimeSpan.FromSeconds(10);
                    var content = new StringContent(xmlPayload, Encoding.UTF8, "application/xml");
                    HttpResponseMessage response = await client.PostAsync(
                        $"{Form1.WSO2_BASE}/wso2/pos/ingreso", content);

                    string result = await response.Content.ReadAsStringAsync();

                    if (response.IsSuccessStatusCode)
                    {
                        txtLog.SelectionColor = Color.FromArgb(0, 230, 120);
                        txtLog.AppendText("\n✅ LOTE REGISTRADO — Stock sumado a la sucursal.\n");
                        txtLog.AppendText(result + "\n");
                        // Limpiar el lote tras el éxito
                        _lote.Clear();
                        lstLote.Items.Clear();
                        lblContador.Text = "0 artículo(s) en el lote";
                    }
                    else
                    {
                        txtLog.SelectionColor = Color.FromArgb(255, 80, 80);
                        txtLog.AppendText($"\n❌ ERROR HTTP {(int)response.StatusCode}\n");
                        txtLog.AppendText(result + "\n");
                        txtLog.AppendText("\nRevise el lote y vuelva a intentarlo.\n");
                    }
                }
            }
            catch (TaskCanceledException)
            {
                txtLog.SelectionColor = Color.FromArgb(255, 200, 0);
                txtLog.AppendText("\n⏱ TIMEOUT: El servidor no respondió.\n");
                txtLog.AppendText("El lote NO fue registrado. Vuelva a intentarlo.\n");
            }
            catch (Exception ex)
            {
                txtLog.SelectionColor = Color.FromArgb(255, 80, 80);
                txtLog.AppendText("\n❌ ERROR DE RED:\n" + ex.Message);
            }

            btnEnviarLote.Enabled = true;
            btnEnviarLote.Text = "📤  REGISTRAR INGRESO DE LOTE";
        }
    }
}
