SELECT year(t.timestamp), month(t.timestamp), day(t.timestamp), 
  count(distinct i.id)/count(distinct t.txid), count(distinct o.id)/count(distinct t.txid)
FROM myblockchain.transaction as t 
  join myblockchain.input as i on t.txid=i.txid 
    join myblockchain.output as o on o.txid = i.txid
group by year(t.timestamp),month(t.timestamp),day(t.timestamp)
order by year(t.timestamp),month(t.timestamp),day(t.timestamp) asc;