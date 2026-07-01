using System;
using System.Drawing;
using System.Windows.Forms;

namespace InnovaTech.POS
{
    public partial class Form1 : Form
    {
        // ID de la sede actual. En producción vendría de un config.ini
        public const string SEDE_ID = "BOD-TIENDA-01";
        public const string CAJA_ID = "CAJA-01";
        // API Gateway: lecturas REST (catálogo, clientes, inventario)
        public const string GATEWAY_BASE = "http://localhost:8080";
        // WSO2: solo para escrituras XML (checkout, ingreso de lote)
        public const string WSO2_BASE = "http://localhost:8290";

        public Form1()
        {
            InitializeComponent();
            ConfigurarUI();
        }

        private void ConfigurarUI()
        {
            this.Text = "InnovaTech POS — Menú Principal";
            this.Size = new Size(460, 340);
            this.BackColor = Color.FromArgb(20, 25, 40);
            this.StartPosition = FormStartPosition.CenterScreen;
            this.FormBorderStyle = FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;

            // ─── Encabezado ───────────────────────────────────────────
            Label lblLogo = new Label()
            {
                Text = "🏪 InnovaTech POS",
                Font = new Font("Segoe UI", 18, FontStyle.Bold),
                ForeColor = Color.White,
                Location = new Point(80, 30),
                AutoSize = true
            };
            this.Controls.Add(lblLogo);

            Label lblSede = new Label()
            {
                Text = $"Sede: {SEDE_ID}   |   {CAJA_ID}",
                Font = new Font("Segoe UI", 9),
                ForeColor = Color.FromArgb(120, 180, 255),
                Location = new Point(130, 70),
                AutoSize = true
            };
            this.Controls.Add(lblSede);

            // ─── Botón Venta ─────────────────────────────────────────
            Button btnVenta = new Button()
            {
                Text = "🛒  REGISTRAR VENTA",
                Location = new Point(90, 120),
                Size = new Size(260, 55),
                Font = new Font("Segoe UI", 12, FontStyle.Bold),
                BackColor = Color.FromArgb(34, 139, 34),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnVenta.FlatAppearance.BorderSize = 0;
            btnVenta.Click += (s, e) =>
            {
                FormVenta fv = new FormVenta();
                fv.ShowDialog();
            };
            this.Controls.Add(btnVenta);

            // ─── Botón Recepción ──────────────────────────────────────
            Button btnRecepcion = new Button()
            {
                Text = "📦  RECIBIR MERCADERÍA",
                Location = new Point(90, 190),
                Size = new Size(260, 55),
                Font = new Font("Segoe UI", 12, FontStyle.Bold),
                BackColor = Color.FromArgb(30, 100, 180),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnRecepcion.FlatAppearance.BorderSize = 0;
            btnRecepcion.Click += (s, e) =>
            {
                FormRecepcion fr = new FormRecepcion();
                fr.ShowDialog();
            };
            this.Controls.Add(btnRecepcion);

            // ─── Pie ──────────────────────────────────────────────────
            Label lblPie = new Label()
            {
                Text = $"Gateway: {GATEWAY_BASE}  |  WSO2: {WSO2_BASE}",
                Font = new Font("Consolas", 8),
                ForeColor = Color.FromArgb(80, 90, 110),
                Location = new Point(140, 270),
                AutoSize = true
            };
            this.Controls.Add(lblPie);
        }
    }
}
