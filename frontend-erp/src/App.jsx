import { useState } from 'react';
import './index.css';

function App() {
  const [formData, setFormData] = useState({
    guiaRemision: '',
    proveedorId: '',
  });
  
  const [articulos, setArticulos] = useState([
    { codigoArticulo: '', cantidadRecibida: '' }
  ]);

  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState(null);

  const handleGeneralChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleArticuloChange = (index, field, value) => {
    const newArticulos = [...articulos];
    newArticulos[index][field] = value;
    setArticulos(newArticulos);
  };

  const addArticulo = () => {
    setArticulos([...articulos, { codigoArticulo: '', cantidadRecibida: '' }]);
  };

  const removeArticulo = (index) => {
    const newArticulos = [...articulos];
    newArticulos.splice(index, 1);
    setArticulos(newArticulos);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setStatus(null);

    const articulosValidos = articulos.filter(a => a.codigoArticulo.trim() !== '' && a.cantidadRecibida > 0);

    if (articulosValidos.length === 0) {
      setStatus({ type: 'error', message: 'Debe ingresar al menos un artículo válido.' });
      setLoading(false);
      return;
    }

    const payload = {
      guiaRemisionId: formData.guiaRemision || `GR-${Math.floor(Math.random() * 10000)}`,
      proveedorId: formData.proveedorId || '20123456789',
      articulos: articulosValidos.map(a => ({
        codigoArticulo: a.codigoArticulo,
        cantidadRecibida: parseInt(a.cantidadRecibida, 10)
      }))
    };

    try {
      const response = await fetch('http://localhost:8086/api/v1/erp/recepcion-mercaderia', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) throw new Error('Error al conectar con el servidor ERP');
      
      const data = await response.json();
      
      setStatus({
        type: 'success',
        message: `¡Éxito! ${data.mensaje} (Guía: ${data.guiaRemisionId})`,
        details: `Se han disparado ${articulosValidos.length} eventos a Kafka.`
      });
      
      setFormData({ guiaRemision: '', proveedorId: '' });
      setArticulos([{ codigoArticulo: '', cantidadRecibida: '' }]);
    } catch (error) {
      setStatus({ type: 'error', message: 'Error de Conexión', details: error.message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-container">
      <div className="header">
        <h1>SAP ERP Central</h1>
        <p>Módulo de Logística: Recepción de Mercadería</p>
      </div>

      <div className="panel">
        <form onSubmit={handleSubmit}>
          <div className="grid-2">
            <div className="form-group">
              <label>Guía de Remisión (Opcional)</label>
              <input type="text" name="guiaRemision" value={formData.guiaRemision} onChange={handleGeneralChange} placeholder="Auto-generada si se omite" />
            </div>
            <div className="form-group">
              <label>RUC Proveedor *</label>
              <input type="text" name="proveedorId" value={formData.proveedorId} onChange={handleGeneralChange} placeholder="Ej: 20123456789" required />
            </div>
          </div>
          
          <div className="articulos-section">
            <div className="articulos-header">
              <label>Detalle de Artículos</label>
              <button type="button" className="btn-small" onClick={addArticulo}>+ Fila</button>
            </div>
            
            {articulos.map((art, idx) => (
              <div className="articulo-row" key={idx}>
                <input type="text" style={{flex: 2}} placeholder="SKU del Producto" value={art.codigoArticulo} onChange={(e) => handleArticuloChange(idx, 'codigoArticulo', e.target.value)} required />
                <input type="number" style={{flex: 1}} placeholder="Cantidad" min="1" value={art.cantidadRecibida} onChange={(e) => handleArticuloChange(idx, 'cantidadRecibida', e.target.value)} required />
                {articulos.length > 1 && (
                  <button type="button" className="btn-remove" onClick={() => removeArticulo(idx)}>×</button>
                )}
              </div>
            ))}
          </div>

          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? <div className="spinner"></div> : 'Registrar Ingreso en SAP (Kafka)'}
          </button>
        </form>

        {status && (
          <div className={`status-message ${status.type === 'success' ? 'status-success' : 'status-error'}`}>
            <div>
              <strong>{status.message}</strong>
              <div style={{fontSize: '0.8rem', marginTop: '0.25rem', opacity: 0.8}}>{status.details}</div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
