package xzf.spiderman.worker.configuration;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.apache.commons.io.IOUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public  class HessianRedisSerializer implements RedisSerializer<Object>
{
    @Override
    public byte[] serialize(Object o) throws SerializationException
    {
        if(o==null){
            return null;
        }

        ByteArrayOutputStream bos = null;
        Hessian2Output hessianOutput = null;
        try
        {
            bos = new ByteArrayOutputStream();
            hessianOutput = new Hessian2Output(bos);
            hessianOutput.writeObject(o);
            hessianOutput.flushBuffer();
            return bos.toByteArray();
        }
        catch (Exception e)
        {
            throw new SerializationException(e.getMessage(),e);
        }
        finally {
            IOUtils.closeQuietly(bos);
            try{hessianOutput.close();}catch (Exception ignore){}
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException
    {
        if(bytes==null){
            return null;
        }

        ByteArrayInputStream bis = null;
        Hessian2Input hessianInput = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            hessianInput = new Hessian2Input(bis);
            return hessianInput.readObject();
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(),e);
        } finally {
           IOUtils.closeQuietly(bis);
           try { hessianInput.close(); } catch (Exception ignore) {}
        }
    }

}