import { useState } from 'react';
import { ShoppingCart, Monitor, CreditCard, Banknote, Store, ScanBarcode } from 'lucide-react';
import './index.css';

const CATALOGO_LOCAL = [
  { sku: 'SKU-LAP-001', nombre: 'Laptop Gamer ASUS', precio: 3500.00 },
  { sku: 'SKU-CEL-002', nombre: 'iPhone 15 Pro Max', precio: 4200.00 },
  { sku: 'SKU-MON-003', nombre: 'Monitor LG 34"', precio: 1200.00 }
];

function App() {
  const [dni, setDni] = useState('');
  const [skuInput, setSkuInput] = useState('');
  const [ticket, setTicket] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modal, setModal] = useState(null);

  const agregarAlTicket = (producto) => {
    setTicket([...ticket, producto]);
  };

  const buscarYAgregarSKU = () => {
    const prod = CATALOGO_LOCAL.find(p => p.sku === skuInput);
    if (prod) {
      agregarAlTicket(prod);
      setSkuInput('');
    } else {
      alert("SKU no encontrado en tienda");
    }
  };

  const total = ticket.reduce((sum, item) => sum + item.precio, 0);

  const cobrar = async (metodo) => {
    if (!dni || ticket.length === 0) return;
    
    setLoading(true);
    const payload = {
      ordenId: `POS-${Math.floor(Math.random() * 10000)}`,
      canalOrigen: 'POS',
      cliente: { numeroDocumento: dni },
      detalles: ticket.map(item => ({
        codigoArticulo: item.sku,
        cantidad: 1,
        precioUnitario: item.precio
      })),
      pago: {
        metodo: metodo, // 'TARJETA_CREDITO' o 'EFECTIVO'
        pasarela: metodo === 'TARJETA_CREDITO' ? 'NIUBIZ' : 'LOCAL',
        montoTotal: total
      }
    };

    try {
      const res = await fetch('http://localhost:8080/api/v1/ventas-pos/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      
      const data = await res.json();
      
      if (res.ok && data.estado === 'APROBADO') {
        setModal({
          success: true,
          title: 'Transacción Aprobada',
          message: 'Boleta emitida. SAGA orquestó el pago y descontó inventario.'
        });
        setTicket([]);
        setDni('');
      } else {
        setModal({
          success: false,
          title: 'Transacción Rechazada',
          message: data.mensajeError || 'La orquestación falló (Rollback ejecutado).'
        });
      }
    } catch (error) {
      setModal({
        success: false,
        title: 'Error de Red',
        message: 'No se pudo contactar al API Gateway (8080).'
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pos-layout">
      {/* Sidebar */}
      <div className="sidebar">
        <div className="sidebar-icon active"><Monitor size={28} /></div>
        <div className="sidebar-icon"><Store size={28} /></div>
        <div className="sidebar-icon"><ShoppingCart size={28} /></div>
      </div>

      {/* Main Area */}
      <div className="main-content">
        <div className="header">
          <h1>Terminal de Caja (POS)</h1>
          <p>Sucursal Lima Sur - Caja 01</p>
        </div>

        <div className="pos-grid">
          {/* Columna Izquierda: Escaneo */}
          <div className="scanner-section">
            <div className="form-group">
              <label>Identificación del Cliente</label>
              <div className="input-row">
                <input type="text" placeholder="DNI o RUC" value={dni} onChange={e => setDni(e.target.value)} />
                <button className="btn-action">Validar</button>
              </div>
            </div>

            <div className="form-group" style={{marginTop: '1rem'}}>
              <label>Escáner de Productos</label>
              <div className="input-row">
                <input type="text" placeholder="Ingresa SKU..." value={skuInput} onChange={e => setSkuInput(e.target.value)} onKeyDown={e => e.key === 'Enter' && buscarYAgregarSKU()} />
                <button className="btn-action" onClick={buscarYAgregarSKU}><ScanBarcode size={20}/></button>
              </div>
            </div>

            <div className="product-list">
              <h3 style={{marginBottom: '1rem', fontSize: '1rem', color: '#64748b'}}>Catálogo Rápido</h3>
              {CATALOGO_LOCAL.map(p => (
                <div className="product-item" key={p.sku}>
                  <div>
                    <div style={{fontWeight: 500}}>{p.nombre}</div>
                    <div style={{fontSize: '0.8rem', color: '#64748b'}}>{p.sku}</div>
                  </div>
                  <button className="btn-add" onClick={() => agregarAlTicket(p)}>Agregar</button>
                </div>
              ))}
            </div>
          </div>

          {/* Columna Derecha: Boleta/Ticket */}
          <div className="ticket-section">
            <div className="ticket-header">
              <h2>INNOVATECH S.A.C.</h2>
              <p style={{fontSize: '0.85rem'}}>Ticket de Venta #10045</p>
            </div>
            
            <div className="ticket-items">
              {ticket.length === 0 ? (
                <p style={{textAlign: 'center', color: '#94a3b8', marginTop: '2rem'}}>Boleta vacía</p>
              ) : (
                ticket.map((item, idx) => (
                  <div className="ticket-row" key={idx}>
                    <span>1x {item.nombre}</span>
                    <span>S/ {item.precio.toFixed(2)}</span>
                  </div>
                ))
              )}
            </div>

            <div className="ticket-totals">
              <div className="total-row">
                <span>TOTAL A PAGAR</span>
                <span>S/ {total.toFixed(2)}</span>
              </div>
              
              <div className="payment-methods">
                <button className="btn-pay card" disabled={ticket.length === 0 || !dni || loading} onClick={() => cobrar('TARJETA_CREDITO')}>
                  <CreditCard size={20}/> Tarjeta
                </button>
                <button className="btn-pay cash" disabled={ticket.length === 0 || !dni || loading} onClick={() => cobrar('EFECTIVO')}>
                  <Banknote size={20}/> Efectivo
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Modal de Resultado */}
      {modal && (
        <div className="overlay">
          <div className="modal">
            <h2 style={{color: modal.success ? '#10b981' : '#ef4444'}}>{modal.title}</h2>
            <p>{modal.message}</p>
            <button className="btn-close" onClick={() => setModal(null)}>Cerrar y Nueva Venta</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
