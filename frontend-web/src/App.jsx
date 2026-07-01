import { useState, useEffect } from 'react';
import './index.css';

const GATEWAY_URL = 'http://localhost:8080/api/v1';

function App() {
  const [productos, setProductos] = useState([]);
  const [carrito, setCarrito] = useState([]);
  const [paso, setPaso] = useState('LOGIN'); // LOGIN, CATALOGO, CHECKOUT
  const [loading, setLoading] = useState(false);
  const [alertMsg, setAlertMsg] = useState(null); // { type: 'success' | 'error', text: '' }

  const [usuario, setUsuario] = useState({
    nombre: "",
    dni: "",
    correo: "",
    direccion: "",
    token: ""
  });
  const [authMode, setAuthMode] = useState('LOGIN'); // LOGIN o REGISTER
  const [authForm, setAuthForm] = useState({
    documentoIdentidad: '',
    nombreCompleto: '',
    correo: '',
    password: '',
    direccion: ''
  });
  const [misPedidos, setMisPedidos] = useState([]);

  useEffect(() => {
    const pedidosGuardados = localStorage.getItem('misPedidos');
    if (pedidosGuardados) {
      setMisPedidos(JSON.parse(pedidosGuardados));
    }
  }, []);

  const handleAuth = async (e) => {
    e.preventDefault();
    setLoading(true);
    setAlertMsg(null);

    const endpoint = authMode === 'LOGIN' ? '/clientes/auth/login' : '/clientes/auth/register';
    
    try {
      const res = await fetch(`${GATEWAY_URL}${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(authForm)
      });
      
      if (res.ok) {
        const data = await res.json();
        setUsuario({
          nombre: data.nombre,
          dni: data.dni,
          correo: data.correo,
          direccion: data.direccion || 'Av. Siempre Viva 123',
          token: data.token
        });
        setPaso('CATALOGO');
      } else {
        const errText = await res.text();
        alert(errText);
      }
    } catch (err) {
      alert("Error conectando al servicio de clientes.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCatalogo();
  }, []);

  const fetchCatalogo = async () => {
    try {
      const res = await fetch(`${GATEWAY_URL}/catalogo/productos`);
      if (res.ok) {
        const data = await res.json();
        if (data && data.length > 0) {
          // Consultar stock a Inventario para cada producto web
          const productosConStock = await Promise.all(data.map(async (prod) => {
            try {
              const stockRes = await fetch(`${GATEWAY_URL}/inventario/${prod.codigoArticulo}?sedeId=BOD-WEB-CENTRAL`);
              let stockWeb = 0;
              if (stockRes.ok) {
                const stockData = await stockRes.json();
                stockWeb = stockData.estadoStock.stockDisponibleVenta;
              }

              const stockTiendaRes = await fetch(`${GATEWAY_URL}/inventario/${prod.codigoArticulo}?sedeId=BOD-TIENDA-01`);
              let stockTienda = 0;
              if (stockTiendaRes.ok) {
                const stockTiendaData = await stockTiendaRes.json();
                stockTienda = stockTiendaData.estadoStock.stockDisponibleVenta;
              }

              return { 
                ...prod, 
                nombre: prod.detalles ? prod.detalles.descripcion : prod.codigoArticulo,
                precio: prod.precios ? prod.precios.precioBase : 0,
                categoria: prod.categoria,
                stock: stockWeb,
                stockTienda: stockTienda
              };
            } catch (e) {
              return { 
                ...prod, 
                nombre: prod.detalles ? prod.detalles.descripcion : prod.codigoArticulo,
                precio: prod.precios ? prod.precios.precioBase : 0,
                categoria: prod.categoria,
                stock: 0,
                stockTienda: 0 
              };
            }
            return { 
              ...prod, 
              nombre: prod.detalles ? prod.detalles.descripcion : prod.codigoArticulo,
              precio: prod.precios ? prod.precios.precioBase : 0,
              categoria: prod.categoria,
              stock: 0 
            };
          }));
          setProductos(productosConStock);
          return;
        }
      }
    } catch (e) {
      console.log("Error o catálogo vacío, usando mock para PoC", e);
    }
    
    // Fallback Mock
    setProductos([
      {
        codigoArticulo: "SKU-LAP-001",
        nombre: "Laptop Gamer ASUS ROG",
        precio: 3500.0,
        categoria: "Laptops",
        stock: 45
      },
      {
        codigoArticulo: "SKU-MON-002",
        nombre: "Monitor LG 27'' 4K",
        precio: 1200.0,
        categoria: "Monitores",
        stock: 12
      }
    ]);
  };

  const agregarAlCarrito = (prod) => {
    if (prod.stock > 0) {
      setCarrito([{ ...prod, cantidadVendida: 1 }]);
      setPaso('CHECKOUT');
      setAlertMsg(null);
    } else {
      setAlertMsg({ type: 'error', text: 'Stock no disponible.' });
    }
  };

  const procesarPago = async (e) => {
    e.preventDefault();
    setLoading(true);
    setAlertMsg(null);

    // Animacion falsa de pasarela
    setTimeout(async () => {
      const payload = {
        clienteId: usuario.correo,
        direccionDestino: usuario.direccion,
        carrito: [
          {
            sku: carrito[0].codigoArticulo,
            cantidad: carrito[0].cantidadVendida
          }
        ],
        datosPago: {
          tokenTarjeta: "TKN-NIUBIZ-" + Math.floor(Math.random() * 10000)
        }
      };

      try {
        const res = await fetch(`${GATEWAY_URL}/ventas-web/checkout`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        
        const data = await res.json();
        if (data.estado === "APROBADO") {
          const trackingMatch = data.mensaje.match(/TRK-OLVA-[A-Z0-9]+/);
          const tracking = trackingMatch ? trackingMatch[0] : "TRK-UNKNOWN";
          
          const nuevoPedido = {
            id: new Date().getTime(),
            fecha: new Date().toLocaleDateString(),
            producto: carrito[0].nombre,
            total: carrito[0].precio,
            trackingId: tracking
          };
          
          const actualizados = [nuevoPedido, ...misPedidos];
          setMisPedidos(actualizados);
          localStorage.setItem('misPedidos', JSON.stringify(actualizados));

          setAlertMsg({ type: 'success', text: data.mensaje || '¡Compra Exitosa! Orden enviada a Olva Courier.' });
          setCarrito([]);
        } else {
          setAlertMsg({ type: 'error', text: data.mensaje || 'Venta Rechazada.' });
        }
      } catch (err) {
        setAlertMsg({ type: 'error', text: 'Error de conexión con el Orquestador Web.' });
      } finally {
        setLoading(false);
      }
    }, 2000);
  };

  return (
    <>
      <nav className="navbar">
        <div className="brand" onClick={() => paso !== 'LOGIN' && setPaso('CATALOGO')}>InnovaTech E-Commerce</div>
        {paso !== 'LOGIN' && (
          <div className="nav-links">
            <span className="nav-link" onClick={() => setPaso('CATALOGO')} style={{cursor: 'pointer'}}>🛍️ Catálogo</span>
            <span className="nav-link" onClick={() => setPaso('PEDIDOS')} style={{cursor: 'pointer'}}>📦 Mis Pedidos</span>
            <span className="nav-link">👤 {usuario.nombre}</span>
            <div className="cart-icon" onClick={() => carrito.length > 0 && setPaso('CHECKOUT')}>
              🛒 <span className="cart-badge">{carrito.length}</span>
            </div>
          </div>
        )}
      </nav>

      <div className="container">
        {paso === 'LOGIN' && (
          <div className="checkout-grid" style={{ display: 'flex', justifyContent: 'center', marginTop: '4rem' }}>
            <div className="checkout-panel" style={{ width: '100%', maxWidth: '400px' }}>
              <h2 style={{ textAlign: 'center' }}>Iniciar Sesión</h2>
              <p style={{ textAlign: 'center', color: 'var(--text-muted)', marginBottom: '1.5rem' }}>Ingresa con tu correo y contraseña</p>
              
              <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem' }}>
                <button 
                  onClick={() => setAuthMode('LOGIN')}
                  style={{ flex: 1, padding: '0.5rem', background: authMode === 'LOGIN' ? 'var(--primary)' : 'transparent', color: authMode === 'LOGIN' ? 'white' : 'var(--text-muted)', border: '1px solid var(--primary)', borderRadius: '4px', cursor: 'pointer' }}>
                  Login
                </button>
                <button 
                  onClick={() => setAuthMode('REGISTER')}
                  style={{ flex: 1, padding: '0.5rem', background: authMode === 'REGISTER' ? 'var(--primary)' : 'transparent', color: authMode === 'REGISTER' ? 'white' : 'var(--text-muted)', border: '1px solid var(--primary)', borderRadius: '4px', cursor: 'pointer' }}>
                  Registro
                </button>
              </div>

              <form onSubmit={handleAuth}>
                {authMode === 'REGISTER' && (
                  <>
                    <div className="form-group">
                      <label>DNI</label>
                      <input type="text" required value={authForm.documentoIdentidad} onChange={e => setAuthForm({...authForm, documentoIdentidad: e.target.value})} />
                    </div>
                    <div className="form-group">
                      <label>Nombre Completo</label>
                      <input type="text" required value={authForm.nombreCompleto} onChange={e => setAuthForm({...authForm, nombreCompleto: e.target.value})} />
                    </div>
                    <div className="form-group">
                      <label>Dirección de Envío</label>
                      <input type="text" required value={authForm.direccion} onChange={e => setAuthForm({...authForm, direccion: e.target.value})} placeholder="Ej: Av. Siempre Viva 123" />
                    </div>
                  </>
                )}
                <div className="form-group">
                  <label>Correo Electrónico</label>
                  <input type="email" required value={authForm.correo} onChange={e => setAuthForm({...authForm, correo: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Contraseña</label>
                  <input type="password" required value={authForm.password} onChange={e => setAuthForm({...authForm, password: e.target.value})} />
                </div>
                
                <button type="submit" className="btn-pay" style={{ background: 'var(--primary)' }} disabled={loading}>
                  {loading ? 'Procesando...' : (authMode === 'LOGIN' ? 'Entrar' : 'Registrarse')}
                </button>
              </form>
            </div>
          </div>
        )}

        {paso === 'CATALOGO' && (
          <>
            <div className="hero">
              <h1>Tecnología que inspira</h1>
              <p>Descubre nuestro catálogo exclusivo conectado en tiempo real al Kardex central de InnovaTech.</p>
            </div>
            
            {alertMsg && (
              <div className={`alert ${alertMsg.type}`}>{alertMsg.text}</div>
            )}

            <div className="product-grid">
              {productos.map(p => (
                <div key={p.codigoArticulo} className="product-card">
                  <div className="product-img">💻</div>
                  <div className="product-content">
                    <span className="product-category">{p.categoria || 'Tecnología'}</span>
                    <h3 className="product-title">{p.nombre}</h3>
                    <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem', justifyContent: 'center' }}>
                      <div style={{ color: p.stock > 0 ? '#10b981' : '#ef4444', fontWeight: 'bold', fontSize: '0.85rem' }}>
                        {p.stock > 0 ? `📦 Web: ${p.stock}` : 'Web: Agotado'}
                      </div>
                      <div style={{ color: p.stockTienda > 0 ? '#3b82f6' : '#ef4444', fontWeight: 'bold', fontSize: '0.85rem' }}>
                        {p.stockTienda > 0 ? `🏪 Tienda: ${p.stockTienda}` : 'Tienda: Agotado'}
                      </div>
                    </div>
                    <p className="product-price">S/ {p.precio.toFixed(2)}</p>
                    <button 
                      className="btn-add" 
                      onClick={() => agregarAlCarrito(p)}
                      disabled={p.stock <= 0}
                    >
                      {p.stock > 0 ? 'Comprar Ahora' : 'Sin Stock'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}

        {paso === 'CHECKOUT' && (
          <div className="checkout-grid">
            <div className="checkout-panel">
              <h2>Tu Carrito</h2>
              {carrito.map(item => (
                <div key={item.codigoArticulo} className="cart-item">
                  <div>
                    <div className="product-title">{item.nombre}</div>
                    <div style={{color: '#64748b'}}>SKU: {item.codigoArticulo}</div>
                  </div>
                  <div className="product-price" style={{margin: 0}}>S/ {item.precio.toFixed(2)}</div>
                </div>
              ))}
              
              {alertMsg && (
                <div className={`alert ${alertMsg.type}`}>{alertMsg.text}</div>
              )}
            </div>

            <div className="checkout-panel">
              <h2>Pasarela de Pago</h2>
              <form onSubmit={procesarPago}>
                <div className="form-group">
                  <label>Número de Tarjeta</label>
                  <input type="text" defaultValue="4555 6666 7777 8888" required />
                </div>
                <div className="form-group">
                  <label>Titular</label>
                  <input type="text" defaultValue={usuario.nombre} required readOnly style={{backgroundColor: '#f1f5f9'}} />
                </div>
                <div className="form-group" style={{display: 'flex', gap: '1rem'}}>
                  <div style={{flex: 1}}>
                    <label>MM/YY</label>
                    <input type="text" defaultValue="12/28" required />
                  </div>
                  <div style={{flex: 1}}>
                    <label>CVC</label>
                    <input type="text" defaultValue="123" required />
                  </div>
                </div>

                <div className="total-row">
                  <span>Total a pagar:</span>
                  <span>S/ {carrito.length > 0 ? carrito[0].precio.toFixed(2) : "0.00"}</span>
                </div>

                <button type="submit" className="btn-pay" disabled={loading || alertMsg?.type === 'success'}>
                  {loading ? 'Procesando SAGA...' : 'Pagar con Niubiz'}
                </button>
                
                {alertMsg?.type === 'success' && (
                  <button type="button" className="btn-add" style={{marginTop: '1rem', background: '#2563eb'}} onClick={() => setPaso('CATALOGO')}>
                    Volver al Catálogo
                  </button>
                )}
              </form>
            </div>
          </div>
        )}

        {paso === 'PEDIDOS' && (
          <div className="checkout-grid" style={{ display: 'flex', justifyContent: 'center', marginTop: '2rem' }}>
            <div className="checkout-panel" style={{ width: '100%', maxWidth: '800px' }}>
              <h2>Mis Pedidos ({misPedidos.length})</h2>
              {misPedidos.length === 0 ? (
                <p style={{ color: 'var(--text-muted)' }}>No tienes pedidos registrados.</p>
              ) : (
                misPedidos.map(pedido => (
                  <div key={pedido.id} className="cart-item" style={{ alignItems: 'center' }}>
                    <div style={{ flex: 1 }}>
                      <div className="product-title">{pedido.producto}</div>
                      <div style={{color: '#64748b', fontSize: '0.85rem'}}>
                        Fecha: {pedido.fecha} | Total: S/ {pedido.total.toFixed(2)}
                      </div>
                      <div style={{color: 'var(--primary)', fontWeight: 'bold', fontSize: '0.85rem'}}>
                        Guía: {pedido.trackingId}
                      </div>
                    </div>
                    <a 
                      href={`${GATEWAY_URL}/transporte/guias/${pedido.trackingId}`} 
                      target="_blank" 
                      rel="noreferrer"
                      style={{ padding: '0.5rem 1rem', textDecoration: 'none', background: '#10b981', color: 'white', border: 'none', borderRadius: '8px', fontWeight: '600', cursor: 'pointer', transition: 'background-color 0.2s', width: 'auto' }}
                    >
                      📄 Ver PDF
                    </a>
                  </div>
                ))
              )}
              <button 
                className="btn-add" 
                style={{ marginTop: '2rem', background: 'var(--text-muted)' }}
                onClick={() => setPaso('CATALOGO')}
              >
                Volver al Catálogo
              </button>
            </div>
          </div>
        )}
      </div>
    </>
  );
}

export default App;
