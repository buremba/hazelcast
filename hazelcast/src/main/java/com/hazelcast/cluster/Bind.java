/*
 * Copyright (c) 2008-2012, Hazel Bilisim Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.cluster;

import com.hazelcast.impl.spi.NoReply;
import com.hazelcast.impl.spi.NodeService;
import com.hazelcast.impl.spi.NonBlockingOperation;
import com.hazelcast.impl.spi.NonMemberOperation;
import com.hazelcast.nio.Address;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Bind extends Master implements NonMemberOperation, NoReply, NonBlockingOperation {

    private Address targetAddress;
    private boolean replyBack = false;

    public Bind() {
    }

    public Bind(Address localAddress) {
        super(localAddress);
        this.targetAddress = null;
    }

    public Bind(Address localAddress, final Address targetAddress, final boolean replyBack) {
        super(localAddress);
        this.targetAddress = targetAddress;
        this.replyBack = replyBack;
    }

    @Override
    public void run() {
        NodeService ns = getNodeService();
        ns.getNode().getConnectionManager().bind(getConnection(), address, targetAddress, replyBack);
    }

    @Override
    public void readInternal(final DataInput in) throws IOException {
        super.readInternal(in);
        boolean hasTarget = in.readBoolean();
        if (hasTarget) {
            targetAddress = new Address();
            targetAddress.readData(in);
        }
        replyBack = in.readBoolean();
    }

    @Override
    public void writeInternal(final DataOutput out) throws IOException {
        super.writeInternal(out);
        boolean hasTarget = targetAddress != null;
        out.writeBoolean(hasTarget);
        if (hasTarget) {
            targetAddress.writeData(out);
        }
        out.writeBoolean(replyBack);
    }

    @Override
    public String toString() {
        return "Bind " + address;
    }
}
